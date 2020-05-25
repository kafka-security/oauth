#!/usr/bin/env python
#
# Copyright 2017 Confluent Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Docker utility belt.

This script contains a set of utility functions required for running docker
containers.

The script has 5 commands:

1. template : Uses Jinja2 and environment variables to generate configuration files.
2. ensure: ensures that a environment variable is set. Used to ensure required properties.
3. ensure-atleast-one: ensures that atleast one of environment variable is set. Used to ensure required properties.
4. wait: waits for a service to become available on a host:port.
5. path: Checks a path for permissions (read, write, execute, exists)

These commands log any output to stderr and returns with exitcode=0 if successful, exitcode=1 otherwise.

"""

from __future__ import print_function
import os
import sys
from jinja2 import Environment, FileSystemLoader
import requests
import socket
import time
import argparse

try:  # Python 2 vs 3
    import urlparse
except ImportError:
    import urllib.parse as urlparse


def env_to_props(env_prefix, prop_prefix, exclude=[]):
    """Converts environment variables with a prefix into key/value properties
        in order to support wildcard handling of properties.  Naming convention
        is to convert env vars to lower case and replace '_' with '.'

    For example: if these are set in the environment
        CONTROL_CENTER_STREAMS_NUM_STREAM_THREADS=4
        CONTROL_CENTER_STREAMS_SECURITY_PROTOCOL=SASL_SS
        CONTROL_CENTER_STREAMS_SASL_KERBEROS_SERVICE_NAME=kafka

        then
            env_to_props('CONTROL_CENTER_STREAMS_', 'confluent.controlcenter.streams.', exclude=['CONTROL_CENTER_STREAMS_NUM_STREAM_THREADS'])
        will produce
            {
                'confluent.controlcenter.streams.security.protocol': 'SASL_SS',
                'confluent.controlcenter.streams.sasl.kerberos.service.name': 'kafka'
            }

    Args:
        env_prefix: prefix of environment variables to include. (e.g. CONTROL_CENTER_STREAMS_)
        prop_prefix: prefix of the resulting properties (e.g. confluent.controlcenter.streams.)
        exclude: list of environment variables to exclude

    Returns:
        Map of matching properties.
    """
    props = {}
    for (env_name, val) in os.environ.items():
        if env_name not in exclude and env_name.startswith(env_prefix):
            prop_name = prop_prefix + '.'.join(env_name[len(env_prefix):].lower().split('_')).replace('..', '_')
            props[prop_name] = val
    return props


def parse_log4j_loggers(overrides_str, defaults={}):
    """Parses *_LOG4J_PROPERTIES string and returns a list of log4j properties.

    For example: if LOG4J_PROPERTIES = "foo.bar=DEBUG,baz.bam=TRACE"
        and
        defaults={"foo.bar: "INFO"}
        the this function will return {"foo.bar: "DEBUG", "baz.bam": "TRACE"}

    Args:
        overrides_str: String containing the overrides for the default properties.
        defaults: Map of default log4j properties.

    Returns:
        Map of log4j properties.

    """
    overrides = overrides_str.split(",")
    if len(overrides) > 0:
        for override in overrides:
            tokens = override.split("=")
            if len(tokens) == 2:
                defaults[tokens[0]] = tokens[1]
    return defaults


def wait_for_service(host, port, timeout):
    """Waits for a service to start listening on a port.

    Args:
        host: Hostname where the service is hosted.
        port: Port where the service is expected to bind.
        timeout: Time in secs to wait for the service to be available.

    Returns:
        False, if the timeout expires and the service is unreachable, True otherwise.

    """
    start = time.time()

    while True:
        try:
            s = socket.create_connection((host, port), timeout)
            s.close()
            return True
        except socket.error:
            pass

        time.sleep(1)

        if time.time() - start > timeout:
            return False


def check_http_ready(url, timeout):
    """Waits for an HTTP/HTTPS URL to be retrievable.

    Useful to wait for a REST API service, for example.

    Args:
        url: URL to retrieve.  Expected HTTP status code: 2xx.
        timeout: Time in secs to wait for the URL to be retrievable.

    Returns:
        False, if the timeout expires and the URL is not retrievable, True otherwise.

    """
    parsed = urlparse.urlparse(url)
    if not parsed.netloc:
        print("URL %s is malformed." % (url,), file=sys.stderr)
        sys.exit(1)
    splitted = parsed.netloc.split(":", 1)
    host = "localhost"
    if len(splitted) > 0:
      host = parsed.netloc.split(":", 1)[0]
    port = 80
    if len(splitted) > 1:
      port = parsed.netloc.split(":", 1)[1]
    else:
      if parsed.scheme == "https":
        port = 443

    # Check if you can connect to the endpoint
    status = wait_for_service(host, port, timeout)

    if status:

        # Check if service is responding as expected
        r = requests.get(url)
        if r.status_code // 100 == 2:
            return True
        else:
            print("Unexpected response for %s, with code: %s and content: %s" % (str(url), str(r.status_code), str(r.text)), file=sys.stderr)
            return False
    else:
        print("%s cannot be reached on port %s." % (str(host), str(port)), file=sys.stderr)
        return False


def check_path_for_permissions(path, mode):
    """Checks a path on the filesystem for permissions. The permissions can be:
        1. writable
        2. readable
        3. executable
        4. existence

    Args:
        path: Full unix path.
        mode: Permission (writable, readable, executable, exists) to check for.

    Returns:
        Returns True if path has the required permissions, False otherwise.

    """
    string_to_mode_map = {"writable": os.W_OK, "readable": os.R_OK, "executable": os.X_OK, "exists": os.F_OK}
    return os.access(path, string_to_mode_map[mode])


def exit_if_absent(env_var):
    """Check if an environment variable is absent.

    Args:
        env_var: Name of environment variable.

    Returns:
        Returns True if env variable exists, False otherwise.

    """
    if not os.environ.get(env_var):
        print("%s is required." % (env_var,), file=sys.stderr)
        return False
    return True


def exit_if_all_absent(env_vars):
    """Check if any of environment variable is present.

    Args:
        env_vars: Names of environment variable.

    Returns:
        Returns True if any of env variable exists, False otherwise.

    """
    for env_var in env_vars:
        if os.environ.get(env_var):
            return True
    print("one of (%s) is required." % (",".join(env_vars),), file=sys.stderr)
    return True


def fill_and_write_template(template_file, output_file, context=os.environ):
    """Uses Jinja2 template and environment variables to create configuration
       files.

       Adds parse_log4j_loggers as a custom function for log4j.property parsing.

    Args:
        template_file: template file path.
        output_file: output file path.
        context: the data for the filling in the template, defaults to environment variables.

    Returns:
        Returns False if an Exception occurs, True otherwise.

    """
    try:
        j2_env = Environment(
            loader=FileSystemLoader(searchpath="/"),
            trim_blocks=True)
        j2_env.globals['parse_log4j_loggers'] = parse_log4j_loggers
        j2_env.globals['env_to_props'] = env_to_props
        with open(output_file, 'w') as f:
            template = j2_env.get_template(template_file)
            f.write(template.render(env=context))

        return True
    except Exception as e:
        print(e, file=sys.stderr)
        return False


def main():
    root = argparse.ArgumentParser(description='Docker Utility Belt.')

    actions = root.add_subparsers(help='Actions', dest='action')

    template = actions.add_parser('template', description='Generate template from env vars.')
    template.add_argument('input', help='Path to template file.')
    template.add_argument('output', help='Path of output file.')

    check_env = actions.add_parser('ensure', description='Check if env var exists.')
    check_env.add_argument('name', help='Name of env var.')

    check_env = actions.add_parser('ensure-atleast-one', description='Check if env var exists.')
    check_env.add_argument('names', nargs='*', help='Names of env var.')

    check_env = actions.add_parser('wait', description='Wait for network service to appear.')
    check_env.add_argument('host', help='Host.')
    check_env.add_argument('port', help='Port.', type=int)
    check_env.add_argument('timeout', help='timeout in secs.', type=float)

    check_env = actions.add_parser('http-ready', description='Wait for an HTTP/HTTPS URL to be retrievable.')
    check_env.add_argument('url', help='URL to retrieve. Expected HTTP status code: 2xx.')
    check_env.add_argument('timeout', help='Time in secs to wait for the URL to be retrievable.', type=float)

    check_env = actions.add_parser('path', description='Check for path permissions and existence.')
    check_env.add_argument('path', help='Full path.')
    check_env.add_argument('mode', help='One of [writable, readable, executable, exists].', choices=['writable', 'readable', 'executable', 'exists'])

    if len(sys.argv) < 2:
        root.print_help()
        sys.exit(1)

    args = root.parse_args()

    success = False

    if args.action == "template":
        success = fill_and_write_template(args.input, args.output)
    elif args.action == "ensure":
        success = exit_if_absent(args.name)
    elif args.action == "ensure-atleast-one":
        success = exit_if_all_absent(args.names)
    elif args.action == "wait":
        success = wait_for_service(args.host, int(args.port), float(args.timeout))
    elif args.action == "http-ready":
        success = check_http_ready(args.url, float(args.timeout))
    elif args.action == "path":
        success = check_path_for_permissions(args.path, args.mode)

    if success:
        sys.exit(0)
    else:
        command = " ".join(sys.argv)
        print("Command [%s] FAILED !" % command, file=sys.stderr)
        sys.exit(1)