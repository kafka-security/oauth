# Development

Lib-Kafka-OAuth is written in the [Java](https://docs.oracle.com/javase/8/docs/technotes/guides/language/) programming language.

If you are not familiar with Java we recommend you read through the Java Tutorials (https://docs.oracle.com/javase/tutorial/index.html) to familiarize yourself with the standard Java development environment.

Requirements:

- Git
- GitHub account (if you are contributing)
- Java Version 8
- Maven

## Getting Started

After cloning the repository, just run `mvn test`. This will:

- Install required dependencies.
- Build the Lib-Kafka-OAuth jar.
- Run all of the tests.

## Workflow

1. Go to [https://github.com//foo](https://github.com/foo) and fork the repository
   into your account by clicking the "Fork" button.

1. Clone the fork to your local machine.

    ```bash
    git clone git@github.com/<GITHUB USERNAME>/foo.git foo
    cd foo
    git remote add upstream https://github.com/foo.git
    ```

2. Create a branch for your changes.

    ```
    git checkout -b somefeature
    ```

3. Update your local branch with upstream.

    ```
    git fetch upstream
    git rebase upstream/master
    ```

4. Develop your changes and regularly update your local branch against upstream.


5. Commit changes and push to your fork.

    ```
    git commit -s
    git push origin somefeature
    ```

6. Submit a Pull Request via https://github.com/\<GITHUB USERNAME>/foo. You
   should be prompted to with a "Compare and Pull Request" button that
   mentions your branch.

7. Once your Pull Request has been reviewed and signed off please squash your
   commits. If you have a specific reason to leave multiple commits in the
   Pull Request, please mention it in the discussion.

   > If you are not familiar with squashing commits, see [the following blog post for a good overview](http://gitready.com/advanced/2009/02/10/squashing-commits-with-rebase.html).
