# omegat-plugins

This repository is to distribute omegat plugins.
These are published in [GitHub Releases](https://github.com/omegat-org/omegat-plugins/releases/tag/continuous-release).

## How to publish your plugin

### Prerequisite

The project order plugin authors  to register their GnuPG/OpenPGP public keys to sign.
A key should have a file name `keyring/<GitHub accout Name>.gpg` such as `keyring/miurahr.gpg`

You can make Pull-Request to add public key, and OmegaT team will check and merge manually.

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

Plugin database is published in [GitHub Releases](https://github.com/omegat-org/omegat-plugins/releases/tag/continuous-release).
A file is in a JSON format.
OmegaT will download the database file and show it on `Preferences->plugin` dialog.
