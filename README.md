# Checking out the Repository
To properly clone this repository, use the --recursive option to clone so that the sso submodule is checked out fully:

```
[sstark@sstark ObsidianToaster]$ git clone --recursive https://github.com/openshiftio-vertx-boosters/vertx-secured-http-booster
Cloning into 'vertx-secured-http-booster'...
remote: Counting objects: 521, done.
remote: Compressing objects: 100% (46/46), done.
remote: Total 521 (delta 12), reused 0 (delta 0), pack-reused 458
Receiving objects: 100% (521/521), 114.07 KiB | 0 bytes/s, done.
Resolving deltas: 100% (181/181), done.
Submodule 'sso' (https://github.com/obsidian-toaster-quickstarts/redhat-sso) registered for path 'sso'
Cloning into 'sso'...
remote: Counting objects: 162, done.
remote: Total 162 (delta 0), reused 0 (delta 0), pack-reused 162
Receiving objects: 100% (162/162), 162.46 KiB | 0 bytes/s, done.
Resolving deltas: 100% (55/55), done.
Submodule path 'sso': checked out 'cd9fbf0980a3930ca7da1f1814a040bf5b032e96'
```

# Full Booster Experience Docs
See the full booster experience docs online at:

<http://appdev.openshift.io/docs/mission-secured-vertx.html>
