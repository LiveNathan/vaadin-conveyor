---

![](../assets/images/blog/github-actions.webp)
Our goal at Hydraulic is to make deploying desktop apps as easy as deploying a web app. The new [Conveyor GitHub Action](https://github.com/hydraulic-software/conveyor/tree/master/actions/build) makes it simple to get continuous
deployment of your app with nothing more than a `git push`. This is especially useful for apps written in languages that
compile ahead of time to native code like C++, Rust or Dart.

The new action takes care of downloading, installing and running Conveyor, as well as caching both the installation and
the build cache, so releasing is faster on subsequent runs. Read on to learn how to use it.

## First step: Build

The first thing you need is the files that you’ll ship. The specifics will depend on the type of project;
for example, our [Flutter Demo](https://github.com/hydraulic-software/flutter-demo) has a GitHub workflow under <a href="https://github.com/hydraulic-software/flutter-demo/blob/master/.github/workflows/build.yml">`.github/workflows/build.yml`</a> that compiles the project.
You can just copy that file into the same location in your own repository to set it up.

Any other build system is also easy. Here’s how to build with Gradle:

```
name: Build

on: [push, workflow_dispatch]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout project sources
      uses: actions/checkout@v3
      
    - name: Setup Gradle
      uses: gradle/gradle-build-action@v2
      
    - name: Run build with Gradle Wrapper
      run: ./gradlew installDist
     
    - name: Upload artifact
      uses: actions/upload-artifact@v3
      with:
          name: build-result
          path: build/install/${YOUR_APP_NAME}/lib
```

## Second step: Package

Add `workflow_call` to the `on:` stanza of `.github/workflows/build.yml`:

```
# ...

on: [push, workflow_dispatch, workflow_call]

# ...
```
Now it can be reused by other workflows.

Add a new workflow at `.github/workflows/deploy.yml` that uses the [Conveyor GitHub Action](https://github.com/hydraulic-software/conveyor/tree/master/actions/build) to package the app built above:

```
name: Deploy
on: [workflow_dispatch]
jobs:
  build:
    # Use the Build workflow described above
    uses: ./.github/workflows/build.yml
    
  deploy:
    needs: [build]
    
    # Important: must be run from Linux.
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v3        
      
      - name: Download artifact
        uses: actions/download-artifact@v3
        with:
          # This is the name of the "Upload artifact" action above.
          name: build-result
          
          # This path is pointed to by your conveyor.conf
          path: ./artifacts
          
      - name: Run Conveyor     
        uses: hydraulic-software/conveyor/actions/build@v9.1
        with:
          command: make site
          signing_key: ${{ secrets.SIGNING_KEY }}
          agree_to_license: 1
```
Be aware that:

- The action runs on Linux. While Conveyor can be also be run from macOS or Windows, Linux workers [cost a lot less](https://docs.github.com/en/billing/managing-billing-for-github-actions/about-billing-for-github-actions#minute-multipliers).
- We’re using the [upload-artifact](https://github.com/actions/upload-artifact) / [download-artifact](https://github.com/actions/download-artifact) actions,
and they only work directly between jobs in the same workflow. If you need to access the artifacts from different workflows, you can use the [nightly.link](https://nightly.link/) service instead.
- You need to provide a [signing key](https://conveyor.hydraulic.dev/latest/configs/#signing), even if self-signing.
When you run Conveyor locally it’ll generate one and write it to your [default config](https://conveyor.hydraulic.dev/latest/configs/#per-user-defaults) (or you can run `conveyor keys generate` explicitly). The key looks like a series of random words with a timestamp at the end, and can be uploaded using the [secrets mechanism](https://docs.github.com/en/actions/security-guides/encrypted-secrets).
- You’re agreeing to the [Conveyor End User License Agreement](https://hydraulic.dev/eula.html) by setting `agree_to_license`. Don’t worry, your firstborns are safe as the EULA is boringly standard.
Now we can point Conveyor at the right files by adding this to the config:

```
app {
  // For frameworks that don't output native files.
  inputs = artifacts

  // Or this if you're using the Flutter build.yml
  windows.amd64.inputs += artifacts/windows
  linux.amd64.inputs += artifacts/build-linux-amd64.tar
  mac.amd64.inputs += artifacts/build-macos-amd64.tar
  mac.aarch64.inputs += artifacts/build-macos-aarch64.tar
}
```

## Third step: Release

The “Run Conveyor” action can also release your app. Here’s
how to publish your release via SFTP by using the `make copied-site` command:

```
# Changes to deploy.yml above
# ...
jobs:
  # ...
  deploy:
    # ...
    steps:
      # ...    
      - name: Set up SSH
        uses: shimataro/ssh-key-action@v2
        with:
          # Always use secrets for sensitive data.
          key: ${{ secrets.SSH_KEY }}
          known_hosts: ${{ secrets.KNOWN_HOSTS }}
          
      - name: Run Conveyor     
        uses: hydraulic-software/conveyor/actions/build@v9.1
        with:
          command: make copied-site
          signing_key: ${{ secrets.SIGNING_KEY }}          
          agree_to_license: 1
          # If your SSH username/host is sensitive, you can hide it behind a secret as well.
          # Otherwise, you can just add it directly to your 'conveyor.conf' file under key 'app.site.copy-to' 
          extra_flags: -Kapp.site.copy-to="${{ secrets.COPY_TO }}"
```
And here’s an example of how to release directly to GitHub:

```
# Changes to deploy.yml above
# ...
jobs:
  # ...
  deploy:
    # ...
    steps:
      # ...
      - name: Run Conveyor
        uses: hydraulic-software/conveyor/actions/build@v9.1
        env:
          # secrets.GITHUB_TOKEN is automatically set up by GitHub itself.
          OAUTH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        with:
          command: make copied-site
          signing_key: ${{ secrets.SIGNING_KEY }}
          agree_to_license: 1
```

```
# Changes to conveyor.conf above
# ...
app {
  # ...
  site {
    github {
      oauth-token = ${env.OAUTH_TOKEN}
      
      # Optional: if you use GitHub Pages, Conveyor will place the generated
      # download.html file and the site icon file there.
      pages-branch = "gh-pages"
    }    
  }
  # ...
}
```
The example will place `download.html`, which will auto-detect the user’s OS and CPU to give them a big green button,
and the site icons into the `gh-pages` branch for your repository. If you prefer to host it outside of GitHub Pages,
you can just copy those files elsewhere.

Finally, if you want to only deploy when pushing to a release branch you can [read the GitHub Actions docs
to learn how](https://docs.github.com/en/actions/using-workflows/events-that-trigger-workflows#running-your-workflow-only-when-a-push-to-specific-branches-occurs).

## And that’s a wrap!

Setting this up isn’t much different to publishing a web app. With continuous deployment from Actions you can easily
release, and by setting the `app.updates = aggressive` key in your `conveyor.conf` you can be sure your users
will always be up-to-date, just like on the web. The difference is that you can now fully exploit all the features of
your language or app framework that wouldn’t be available inside the browser, as well as enjoy the peace of mind that
comes from knowing that there are no web servers that can get hacked: as long as your code signing key is properly locked
down as a workflow secret, all code you’re shipping will reflect the security policies imposed on your repositories.



---