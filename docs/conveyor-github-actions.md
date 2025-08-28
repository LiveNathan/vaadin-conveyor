<!-- <nav> -->
<a href="https://www.hydraulic.dev/">![logo](../images/logo-white.svg)</a> Hydraulic Conveyor
- Welcome
<!-- <nav> -->
Welcome
  - [Getting started](../../../../../..)
  - [Sample apps](../sample-apps/)
  - [Download Conveyor](../download-conveyor/)
  - Using Conveyor
<!-- <nav> -->
Using Conveyor
    - [Running Conveyor](../running/)
    - Continuous integration [Continuous integration](./)
<!-- <nav> -->
Table of contents
      - [Configuring CI for Conveyor](about:blank#configuring-ci-for-conveyor)
      - [Credentials](about:blank#credentials)
      - [Caching Conveyor downloads](about:blank#caching-conveyor-downloads)
      - [Worker system requirements](about:blank#worker-system-requirements)
      - [Disk cache](about:blank#disk-cache)
      - [macOS keychain access](about:blank#macos-keychain-access)
      - [Forcing re-downloads of artifacts](about:blank#forcing-re-downloads-of-artifacts)
      - [Using GitHub Actions](about:blank#using-github-actions)
<!-- <nav> -->
        - [Building from GitHub Actions, running Conveyor outside](about:blank#building-from-github-actions-running-conveyor-outside)
        - [Running Conveyor from within GitHub Actions](about:blank#running-conveyor-from-within-github-actions)
<!-- <nav> -->
          - [Apple Notarization](about:blank#apple-notarization)

<!-- </nav> -->

<!-- </nav> -->

<!-- </nav> -->
    - [Build performance](../performance/)
    - [Configuring a CDN](../configuring-cdns/)
    - [Migrating Electron apps](../migrating-electron-apps/)

<!-- </nav> -->
  - Results
<!-- <nav> -->
Results
    - [Package formats](../package-formats/)
    - [Understanding updates](../understanding-updates/)
    - [Uploading packages](../serving/uploading/)

<!-- </nav> -->
  - Update control API
<!-- <nav> -->
Update control API
    - [JVM API](../control-api-jvm/)
    - [Electron API](../control-api-electron/)
    - [Native API](../control-api-native/)

<!-- </nav> -->
  - Comparison vs alternatives
<!-- <nav> -->
Comparison vs alternatives
    - [Electron](../comparisons/electron-comparisons/)
    - [JVM](../comparisons/jvm-comparisons/)

<!-- </nav> -->
  - Versions
<!-- <nav> -->
Versions
    - [Release notes](../release-notes/)
    - [Known issues](../known-issues/)
    - [Compatibility levels](../compatibility-levels/)
    - [Minimum system requirements](../min-sys-requirements/)

<!-- </nav> -->

<!-- </nav> -->
- Tutorial
<!-- <nav> -->
Tutorial
  - [Choose your path](../tutorial/new/)
  - The Hare
<!-- <nav> -->
The Hare
    - [Electron](../tutorial/hare/electron/)
    - [JVM apps](../tutorial/hare/jvm/)
    - [Flutter](../tutorial/hare/flutter/)
    - [Native apps](../tutorial/hare/native/)

<!-- </nav> -->
  - The Tortoise
<!-- <nav> -->
The Tortoise
    - [1. Get started](../tutorial/tortoise/1-get-started/)
    - [2. Create or adapt a project](../tutorial/tortoise/2-create-or-adapt-a-project/)
    - [3. Compile the app](../tutorial/tortoise/3-compile/)
    - [4. Build un-packaged apps](../tutorial/tortoise/4-build-unpackaged/)
    - [5. Serve the download site](../tutorial/tortoise/5-serve-the-site/)
    - [6. Install the app](../tutorial/tortoise/6-install-the-app/)
    - [7. Release an update](../tutorial/tortoise/7-release-an-update/)
    - [8. Set a real site URL](../tutorial/tortoise/8-site/)
    - [9. Next steps](../tutorial/tortoise/9-next-steps/)

<!-- </nav> -->

<!-- </nav> -->
- Configuration
<!-- <nav> -->
Configuration
  - [Basic configuration](../configs/)
  - [Names and metadata](../configs/names/)
  - [Icons](../configs/icons/)
  - [Inputs](../configs/inputs/)
  - [Update modes](../configs/update-modes/)
  - [OS integration](../configs/os-integration/)
  - [Keys and certificates](../configs/keys-and-certificates/)
  - [Config Library](../stdlib/)
  - Syntax
<!-- <nav> -->
Syntax
    - [Tutorial and extensions](../configs/hocon/)
    - [Reference](../configs/hocon-spec/)

<!-- </nav> -->
  - Runtimes
<!-- <nav> -->
Runtimes
    - [Native apps](../configs/native-apps/)
    - [Electron](../configs/electron/)
    - [JVM](../configs/jvm/)
    - [Flutter](../configs/flutter/)

<!-- </nav> -->
  - Operating systems
<!-- <nav> -->
Operating systems
    - [Windows](../configs/windows/)
    - [Mac](../configs/mac/)
    - [Linux](../configs/linux/)

<!-- </nav> -->
  - Build systems
<!-- <nav> -->
Build systems
    - [Maven and Gradle](../configs/maven-gradle/)

<!-- </nav> -->

<!-- </nav> -->
- FAQ
<!-- <nav> -->
FAQ
  - [General FAQ](../faq/)
  - [Output formats](../faq/output-formats/)
  - [Making packages](../faq/making-packages/)
  - [Signing and certificates](../faq/signing-and-certificates/)

<!-- </nav> -->
- Troubleshooting
<!-- <nav> -->
Troubleshooting
  - [Building](../troubleshooting/troubleshooting-builds/)
  - [Windows](../troubleshooting/troubleshooting-windows/)
  - [Linux](../troubleshooting/troubleshooting-linux/)
  - [JVM apps](../troubleshooting/troubleshooting-jvm/)

<!-- </nav> -->

<!-- </nav> -->

<!-- <nav> -->
Table of contents
- [Configuring CI for Conveyor](about:blank#configuring-ci-for-conveyor)
- [Credentials](about:blank#credentials)
- [Caching Conveyor downloads](about:blank#caching-conveyor-downloads)
- [Worker system requirements](about:blank#worker-system-requirements)
- [Disk cache](about:blank#disk-cache)
- [macOS keychain access](about:blank#macos-keychain-access)
- [Forcing re-downloads of artifacts](about:blank#forcing-re-downloads-of-artifacts)
- [Using GitHub Actions](about:blank#using-github-actions)
<!-- <nav> -->
  - [Building from GitHub Actions, running Conveyor outside](about:blank#building-from-github-actions-running-conveyor-outside)
  - [Running Conveyor from within GitHub Actions](about:blank#running-conveyor-from-within-github-actions)
<!-- <nav> -->
    - [Apple Notarization](about:blank#apple-notarization)

<!-- </nav> -->

<!-- </nav> -->

<!-- </nav> -->

# Continuous integration [¶](about:blank#continuous-integration)

Looking for GitHub Actions?

Conveyor has an [Action ready for you to use](about:blank#using-github-actions), and [a blog post about how to use it](https://hydraulic.dev/blog/12-github-action.html).

## Configuring CI for Conveyor [¶](about:blank#configuring-ci-for-conveyor)

Conveyor works well with CI platforms like TeamCity, GitHub Actions etc. Pay attention to the following.

## Credentials [¶](about:blank#credentials)

When building in CI you should supply your signing credentials in a different way than using the `defaults.conf` file. A simple approach is to create a separate file next to your main `conveyor.conf` file that looks like this:

```
include required("conveyor.conf")

app {
    signing-key = ${env.SIGNING_KEY}

    mac.certificate = apple.cer
    windows.certificate = windows.cer

    mac.notarization {
        app-specific-password = ${env.APPLE_ASP}
        team-id = 6MD7Z8H86K
        apple-id = "you@user.host"
    }
}
```
Call it something like `ci.conveyor.conf`. Copy your `.cer` / `.pem` files to be next to this file (or adjust the paths). Now place your root key and Apple notarization app-specific password into secret environment variables in your CI configuration called `SIGNING_KEY` and `APPLE_ASP` respectively. Finally, invoke conveyor like this: `conveyor -f ci.conveyor.conf make site`. Your main `conveyor.conf` file will fall back to the generated defaults for self-signing.

An alternative approach is to set a passphrase, then put the encrypted `app.signing-key` value into your main app config that gets checked into version control. You can then put the passphrase into an environment variable and specify it on the command line with `--passphrase=env:PASSPHRASE`.

## Caching Conveyor downloads [¶](about:blank#caching-conveyor-downloads)

To get Conveyor onto your build workers, either download the Linux tarball or pre-install it on your agents. You can get a link for the current version from the [download page](https://downloads.hydraulic.dev/conveyor/download.html), which will look like this: `https://downloads.hydraulic.dev/conveyor/conveyor-${CONVEYOR_VERSION}-linux-amd64.tar.gz`.

Caching Conveyor downloads

Please be careful that your CI/build system doesn't download Conveyor over and over again. If you can't pre-install it on your workers for some reason, make sure the download is cached locally. IP addresses that seem to be re-downloading Conveyor on every build may be throttled or blocked.

## Worker system requirements [¶](about:blank#worker-system-requirements)

Conveyor is quite resource intensive. You should ensure your workers have:

- At least 11GB of free disk space *after* installing Conveyor itself. That's because the [disk cache](../running/#the-cache) is sized at 10GB by default and Conveyor will try to keep at least 1GB of free space available, so this size ensures it won't delete cache entries unnecessarily. You can adjust the cache size from the command line.
- At least 32GB of free RAM.
- A fast filing system (ideally, not Windows)
- Plenty of CPU.
- Internet access.
You can reduce the resource requirements by passing `--parallelism=2` (or lower) as a flag, to lower the number of tasks run simultaneously. This will slow down the build but make it fit into smaller workers.

## Disk cache [¶](about:blank#disk-cache)

It is *strongly recommended* that you preserve the contents of Conveyor's [disk cache](../running/#the-cache) directory between builds, ideally by leaving the files on disk and running builds on the same worker. When not possible, backing up and restoring the cache is second best approach (the GitHub Action is configured to do this by default). Preserving the cache has three advantages:

1. It speeds up releasing the next version significantly, by avoiding the need to redownload large files (e.g. previous versions of your app for delta computation).
2. It will reduce the number of signatures you consume, if you're using cloud signing services.

## macOS keychain access [¶](about:blank#macos-keychain-access)

If you have the ability to build on a Mac then it's worth thinking about doing it, because the macOS keychain is a good way to keep your root key secure. When the key is stored there the OS will only supply it to Conveyor and no other app. Additionally, the OS will prevent Conveyor from being patched or overwritten on disk by other software.

Normally the keychain is unlocked by physically logging in to the machine. You can unlock it for a remote session (e.g. via ssh) by using the `security unlock-keychain` command before running Conveyor.

## Forcing re-downloads of artifacts [¶](about:blank#forcing-re-downloads-of-artifacts)

Conveyor assumes by default that input URLs are stable/versioned and caches them. It doesn't at this time use HTTP caching, so if you need to rebuild your package because the contents of your input URLs changed without the URL itself changing add the `--rerun=all` flag at the end of your command line:

```
conveyor make site --rerun=all
```

## Using GitHub Actions [¶](about:blank#using-github-actions)

### Building from GitHub Actions, running Conveyor outside [¶](about:blank#building-from-github-actions-running-conveyor-outside)

You may want to run Conveyor locally if you have a code signing USB key. Downloading files from GitHub Actions has a couple of limitations that require workarounds:

1. No direct download links for artifacts exported from jobs.
2. Can only export files using zips, and those zips don't preserve UNIX file permissions.
An example of how to use GitHub Actions with Conveyor is [the GitHub Desktop package](https://github.com/hydraulic-software/github-desktop/). It does the build of the platform-specific artifacts in Actions on each commit, but expects you to run Conveyor locally when it's time to release.

- <a href="https://github.com/hydraulic-software/github-desktop/blob/conveyorize/conveyor.conf">`conveyor.conf`</a>
- <a href="https://github.com/hydraulic-software/github-desktop/blob/conveyorize/.github/workflows/ci.yml">`ci.yml`</a>
It solves the above limitations:

1. A direct download link to the output of a CI build job is created using the <a href="https://www.nightly.link/">`nightly.link`</a> service. You give this website the URL of your Actions job YAML, and it gives you back download links that can be used as Conveyor inputs.
2. UNIX files are wrapped in a tarball which is then in turn exported inside a zip. Windows files are exported directly inside a zip. Conveyor is then told to extract the archives and inner archives to get at the files.
The config for this looks like:

```
ci-artifacts-url = nightly.link/hydraulic-software/github-desktop/workflows/ci/conveyorize

app {
  windows.amd64.inputs = ${ci-artifacts-url}/build-out-Windows-x64.zip
  mac.amd64.inputs = [{
    from = ${ci-artifacts-url}/build-out-macOS-x64.zip
    extract = 2
  }]
  mac.aarch64.inputs = [{
    from = ${ci-artifacts-url}/build-out-macOS-arm64.zip
    extract = 2
  }]
}
```
By defining the inputs as an object and then using the `extract` key, the outer zip and inner tarball can be both unwrapped. This preserves file permissions and other UNIX metadata.

### Running Conveyor from within GitHub Actions [¶](about:blank#running-conveyor-from-within-github-actions)

You can use the [Conveyor GitHub Action](https://github.com/hydraulic-software/conveyor/tree/master/actions/build) to perform releases directly from GitHub.

These workflows contain examples of how to use the Conveyor action:

- [Deploy via SSH](https://github.com/hydraulic-software/flutter-demo/tree/master/.github/workflows/deploy-to-ssh.yml)
- [Deploy to GitHub Releases](https://github.com/hydraulic-software/flutter-demo/tree/master/.github/workflows/deploy-to-gh.yml)
You will need to change your `conveyor.conf` to point your inputs to the
paths specified in the `download-artifact` steps:

```
app {
  windows.amd64.inputs += artifacts/windows
  linux.amd64.inputs += artifacts/build-linux-amd64.tar
  mac.amd64.inputs += artifacts/build-macos-amd64.tar
  mac.aarch64.inputs += artifacts/build-macos-aarch64.tar  
}
```
You'll also need to [retrieve your root key](../configs/keys-and-certificates/#exporting-your-root-key) and store it as a [secret](https://docs.github.com/en/actions/security-guides/encrypted-secrets).
The deployment workflows above wire the secret into the Conveyor GitHub Action via the `signing_key` parameter:

```
(...)
- name: Run Conveyor     
  uses: hydraulic-software/conveyor/actions/build@v19.0
  with:
    command: make copied-site
    # This example uses a secret named SIGNING_KEY.
    signing_key: ${{ secrets.SIGNING_KEY }}
    agree_to_license: 1
```
To release Windows apps with hardware protected keys you have a few options:

1. Run Conveyor locally instead of driving it from CI, with your key HSM plugged in via USB.
2. Provide GitHub or your CI system with a build agent that has the signing key plugged in, and supply the passphrase via a secret environment variable (e.g. `--passphrase=env:SIGNING_PASSPHRASE`).
3. Use a cloud HSM or signing service like [SignPath](https://about.signpath.io/)

#### Apple Notarization [¶](about:blank#apple-notarization)

You can also run notarization from GitHub Actions. The process is very similar to the general one described above for CI in general; just copy the certificate files and edit `ci.conveyor.conf` with the notarization data.
The sensitive info can be stored in a [secret](https://docs.github.com/en/actions/security-guides/encrypted-secrets).
A few tweaks in the deployment script are necessary to wire the data into the GitHub action, like setting up secrets in env vars and passing additional command line flags.

For example, for the `ci.conveyor.conf` file below:

```
include required("conveyor.conf")

app {
    mac.certificate = apple.cer
    windows.certificate = windows.cer

    mac.notarization {
        app-specific-password = ${env.APPLE_ASP}
        team-id = 6MD7Z8H86K
        apple-id = "you@user.host"
    }
}
```
You can change your deployment workflow like this:

```
(...)
- name: Run Conveyor     
  uses: hydraulic-software/conveyor/actions/build@v19.0
  env:
    # The app specific password in this example was  
    # stored into a secret named APPLE_ASP.
    APPLE_ASP: ${{ secrets.APPLE_ASP }}

    # Any other secrets can be passed into Conveyor 
    # as environment variables using this mechanism,
    # and refered to from the .conf file using the 
    # syntax ${env.VARIABLE_NAME}.

  with:
    command: make copied-site

    # Tell Conveyor to load the alternative CI config.
    extra_flags: -f ci.conveyor.conf

    # The signing key is an action parameter,
    # because it is mandatory.
    signing_key: ${{ secrets.SIGNING_KEY }}

    agree_to_license: 1
```