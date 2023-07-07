# omegat-plugins

This repository is to distribute certified omegat plugins.

## How to publish your plugin

### Prerequisite

The project orders plugin authors to register their GnuPG/OpenPGP public keys in GitHub accout.
Please check GitHub documentations.

- [Adding a GPG key to your GitHub account](https://docs.github.com/en/authentication/managing-commit-signature-verification/adding-a-gpg-key-to-your-github-account)
- [Generating a new GPG key](https://docs.github.com/en/authentication/managing-commit-signature-verification/generating-a-new-gpg-key)

### Mandatory manifest attributes

Following attributes are mandatory to publish your plugin in OmegaT repository.

- "Name"
- "Version" 
- "Author"
- "Category"

### make pull-request to push plugin jar file

Plugin developers who want to add your plugin to repository, please raise a pull-request
to add your plugin jar file under `plugins/` folder.

You should add just TWO files.

- Plugin jar file (file end with .jar)
- Plugin jar sign file (file end with .jar.asc)

A signature file can be created with Gradle signing plugin, or made manually with command line.

When you have a plugin file named `omegat-some-plugin-1.2.3.jar`, you can run

```commandline
gpg --armor --detach-sign omegat-some-plugin-1.2.3.jar
```

When you have `build.gradle.kts` in your plugin project, it may become like

```kotlin
plugins {
    java
    signing
}
val signKey = listOf("signingKey", "signing.keyId", "signing.gnupg.keyName").find {project.hasProperty(it)}
tasks.withType<Sign> {
    onlyIf { signKey != null }
}
signing {
    when (signKey) {
        "signingKey" -> {
            val signingKey: String? by project
            val signingPassword: String? by project
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
        "signing.keyId" -> {/* do nothing */
        }
        "signing.gnupg.keyName" -> {
            useGpgCmd()
        }
    }
    sign(tasks.jar.get())
}
```

### check workflows

OmegaT set up an automation workflows to check manifest and jar signature.
An actor who creates a Pull-Request should be as same as a signer of a signature.

After all the checks are passed, your PR will be merged into a main branch.


## Continuous publish of a plugin database

Plugin database is published [OmegaT web page](https://omegat.sourceforge.io/plugins/plugins.json). A file is in a JSON format.
OmegaT will download the database file and show it on `Preferences->plugin` dialog.
