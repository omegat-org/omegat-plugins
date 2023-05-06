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

### check workflows

OmegaT set up an automation workflows to check manifest and jar signature.
An actor who creates Pull-Request should be as same as a signer of a signature.

After all the checks are passed, your PR will be merged into a main branch.


## Continuous publish of a plugin database

Plugin database is published in [GitHub Releases](https://github.com/omegat-org/omegat-plugins/releases/tag/continuous-release).
A file is in a JSON format.
OmegaT will download the database file and show it on `Preferences->plugin` dialog.
