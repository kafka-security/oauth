# Contributing

Thanks for your interest in contributing to the Lib-Kafka-OAuth project!

## Getting Started

------

If you have questions, comments, or requests feel free to create an issue on GitHub.

If you want to contribute code and you are new to the Java programming language, check out
the [DEVELOPMENT.md](./DEVELOPMENT.md) reference for help getting started.

We currently welcome contributions of all kinds. For example:

- Development of features, bug fixes, and other improvements.
- Documentation including reference material and examples.
- Bug and feature reports.

## Contribution process

------

Small bug fixes (or other small improvements) can be submitted directly via a Pull Request on GitHub.
You can expect at least one of the Lib-Kafka-OAuth maintainers to respond quickly.

Before submitting large changes, please open an issue on GitHub outlining:

- The use case that your changes are applicable to.
- Steps to reproduce the issue(s) if applicable.
- Detailed description of what your changes would entail.
- Alternative solutions or approaches if applicable.

Use your judgement about what constitutes a large change. If you aren't sure submit an issue on GitHub.

### Code Contributions

------

If you are contributing code, please consider the following:

- Most changes should be accompanied with tests.
- All commits must be signed off (see next section).
- Related commits must be squashed before they are merged.
- All tests must pass.

Avoid adding third-party dependencies (_vendoring_). Lib-Kafka-OAuth is designed to be minimal,
lightweight, and easily embedded. **Vendoring** may make features _easier_ to
implement however they come with their own cost for both Lib-Kafka-OAuth developers and
Lib-Kafka-OAuth users (e.g., _vendoring_ conflicts, security, debugging, etc.)

### Commit Messages

------

Commit messages should explain *why* the changes were made and should follow The Conventional Commits spec below.  The specification is a lightweight convention on top of commit messages. It provides an _easy set of rules_ for creating an explicit commit history; which makes it *easier to write automated tools* on top of. This convention dovetails with SemVer, by describing the features, fixes, and breaking changes made in commit messages.

The commit message should be structured as follows:

```bash
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

The commit contains the following structural elements, to communicate intent to the consumers of your library:

1. **fix:** a commit of the type fix patches a bug in your codebase (this correlates with [PATCH](http://semver.org/#summary) in semantic versioning).

2. **feat:** a commit of the type feat introduces a new feature to the codebase (this correlates with MINOR in semantic versioning).

3. **BREAKING CHANGE:** a commit that has a footer `BREAKING CHANGE:`, or appends a `!` after the _type/scope_, introduces a breaking API change (correlating with MAJOR in semantic versioning). A `BREAKING CHANGE` can be part of commits of any type.

4. **types** other than `fix:` and `feat:` are allowed, for example `@commitlint/config-conventional` recommends `build:`, `chore:`, `ci:`, `docs:`, `style:`, `refactor:`, `perf:`, `test:`, and others.

5. **footers** other than `BREAKING CHANGE: <description>` may be provided and follow a convention similar to [git trailer format](https://git-scm.com/docs/git-interpret-trailers).

If your changes are related to an **open issue** (bug or feature), please include the following line at the end of your commit message:

```bash
Last Line: Fixes #<ISSUE_NUMBER>
```

Additional types are not mandated by the conventional commits specification, and have no implicit effect in semantic versioning (unless they include a BREAKING CHANGE).

A scope may be provided to a commits `type`, to provide additional contextual information and is contained within parenthesis, e.g., `feat(parser): add ability to parse arrays.`

> Why Use Conventional Commits?

1. Auto generating CHANGELOGs.
2. Automatically determines a `semantic version` bump (based on the types of commits landed).
3. Communicate the nature of changes to teammates, the community, and other stakeholders.
4. Triggering build and publish processes.
5. Makes it easier for people to contribute to your projects, by allowing them to explore a more structured commit history.

#### Developer Certificate Of Origin

------

The Lib-Kafka-OAuth project requires that contributors sign off on changes submitted to Lib-Kafka-OAuth repositories.
The [Developer Certificate of Origin (DCO)](https://developercertificate.org/) is a simple way to certify that you wrote or have the right to submit the code you are contributing to the project.

The `DCO` is a standard requirement for `Linux Foundation` and `CNCF projects`.

You _sign-off_ by adding the following to the footer of your commit message manually:

```bash
feat: some new feature

...
Signed-off-by: Random J Developer <random@developer.example.org>
```

Moreover, Git has a `-s, --signoff` commit sub-command option to add `add Signed-off-by:` automatically.

```bash
git commit -s -m 'feat: some new feature'
```

#### More Examples

------

Commit message with description and breaking change footer.

```bash
feat: allow provided config object to extend other configs

BREAKING CHANGE: `extends` key in config file is now used for extending other config files
```

Commit message with `!` to draw attention to breaking change.

```bash
refactor!: drop support for Node 8
```

Commit message with both `!` and BREAKING CHANGE footer.

```bash
refactor!: drop support for Node 8

BREAKING CHANGE: refactor to use JavaScript features not available in Node 8.
```

Commit message with no body.

```bash
docs: correct spelling of CHANGELOG
```

Commit message with scope.

```bash
feat(lang): add polish language
```

Commit message with multi-paragraph body and multiple footers.

```bash
fix: correct minor typos in code

see the issue for details on typos fixed.

follows on additional documentation updates as part of refs below.

Reviewed-by: Z
Signed-off-by: Random J Developer <random@developer.example.org>
Refs #133
Fixes #244
```

You can find the full text of the DCO [here](https://developercertificate.org/)

### Code Review

------

Before a Pull Request is merged, it will undergo code review from other members
of the Lib-Kafka-OAuth community. In order to streamline the code review process, when
amending your Pull Request in response to a review, do not squash your changes
into relevant commits until it has been approved for merge. This allows the
reviewer to see what changes are new and removes the need to wade through code
that has not been modified to search for a small change.

When adding temporary patches in response to review comments, consider
formatting the message description like one of the following:

- `chore: fixup into commit <commit ID> (squash before merge)`
- `chore: fixed changes requested by @username (squash before merge)`
- `chore: amended <description> (squash before merge)`

The purpose of these formats is to provide some context into the reason the
temporary commit exists, and to label it as needing squashed before a merge
is performed.

It is worth noting that not all changes need be squashed before a merge is
performed. Some changes made as a result of review stand well on their own,
independent of other commits in the series. Such changes should be made into
their own commit and added to the PR.

If your Pull Request is small though, it is acceptable to squash changes during
the review process. Use your judgement about what constitutes a _small_ `Pull
Request`.  If you aren't sure, post a comment on the Pull Request.
