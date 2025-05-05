# [1.8.0](https://github.com/GeorgeV220/cosmicjars/compare/v1.7.0...v1.8.0) (2025-05-05)


### Features

* improve server stop handling and memory management ([05ec19e](https://github.com/GeorgeV220/cosmicjars/commit/05ec19ee83691f0a627396b20b35efe577564378))
* **JDKUtilities:** support multi-platform JDK management and improve handling ([d773804](https://github.com/GeorgeV220/cosmicjars/commit/d7738048225bc3822e6afb735e34c8f5e89e5403))
* **platform:** Improve architecture detection ([#24](https://github.com/GeorgeV220/cosmicjars/issues/24)) ([9d36fef](https://github.com/GeorgeV220/cosmicjars/commit/9d36fef516451e662fbf6123243158a0196d926e))
* **providers:** refactor PandaSpigotProvider to use metadata API and build tracking ([642206c](https://github.com/GeorgeV220/cosmicjars/commit/642206cd49fc23872c0762de0445482bc94917a8))

# [1.7.0](https://github.com/GeorgeV220/cosmicjars/compare/v1.6.0...v1.7.0) (2025-03-21)


### Bug Fixes

* initialize MinecraftServer when null and expose arguments ([c2d852e](https://github.com/GeorgeV220/cosmicjars/commit/c2d852e874da60f314e0cdaa8f7b3d0413e0450d))
* update config key from 'server.type' to 'server.implementation' ([8fa94f3](https://github.com/GeorgeV220/cosmicjars/commit/8fa94f3711da9dc8ebccffffec06b7baaee96ea5))


### Features

* **gui:** initialize dropdowns with config values in ConfigPopup ([66cd8fc](https://github.com/GeorgeV220/cosmicjars/commit/66cd8fc39adc6b333edf52cf68f15a0b301e4144))
* **ui:** Add macOS system properties for JMenuBar context menu ([02db9ca](https://github.com/GeorgeV220/cosmicjars/commit/02db9ca168e2905af4987c38d09473041d2d73f0))

# [1.6.0](https://github.com/GeorgeV220/cosmicjars/compare/v1.5.0...v1.6.0) (2025-03-21)


### Bug Fixes

* convert server type and implementation to lowercase in config ([1c5a298](https://github.com/GeorgeV220/cosmicjars/commit/1c5a2985494fe33ce504c2dfa6c146d110ca737a))


### Features

* add command manager and refactor command handling ([e3065e7](https://github.com/GeorgeV220/cosmicjars/commit/e3065e760fd5cd3f7d64c725ba573af045d07cf6))
* **minecraft-server:** add stopServer method to handle server shutdown ([a57da82](https://github.com/GeorgeV220/cosmicjars/commit/a57da82be8e3c72dae9a406370ab6a90c1b7dded))
* **ui:** add floating control panel with server management buttons ([7f26c28](https://github.com/GeorgeV220/cosmicjars/commit/7f26c2810aa8674b107d96bb9283d4a342c9a83b))
* update CosmicJars startup banner to ASCII art ([478d80a](https://github.com/GeorgeV220/cosmicjars/commit/478d80a3829878cb8fa2c390604bbc06d39f9d82))

# [1.5.0](https://github.com/GeorgeV220/cosmicjars/compare/v1.4.0...v1.5.0) (2025-03-12)


### Features

* add ConfigPopup class for server configuration GUI ([239123e](https://github.com/GeorgeV220/cosmicjars/commit/239123e591eae45201962a9221fa7d2be87af5a1))
* add menu bar with file and help options to CosmicJarsFrame ([2e9c7c6](https://github.com/GeorgeV220/cosmicjars/commit/2e9c7c6368011826b81fe9785d2300ed2fa5c9f2))
* add method to retrieve JDK versions from Adoptium API ([c4d24f8](https://github.com/GeorgeV220/cosmicjars/commit/c4d24f8ae64f93be902924e58dc2eda126c99858))
* add method to retrieve manifest attribute values ([81170b1](https://github.com/GeorgeV220/cosmicjars/commit/81170b13c93c777501af8b5a2996755791ef509b))
* add server process cleanup before exit in CosmicJarsFrame ([75e93cb](https://github.com/GeorgeV220/cosmicjars/commit/75e93cb15d0bb04f6aaad1982b549538f7a098a8))
* **build.gradle:** add version, git repo, and build timestamp to manifest ([349154b](https://github.com/GeorgeV220/cosmicjars/commit/349154babf687af493293ef0485805217793f498))

# [1.4.0](https://github.com/GeorgeV220/cosmicjars/compare/v1.3.0...v1.4.0) (2025-03-08)


### Features

* Add MCJars provider implementation and info classes ([7861056](https://github.com/GeorgeV220/cosmicjars/commit/7861056dd8475218347e20c5e1cbc3ea8302d270))
* normalize server implementation case in Provider.java ([0f51b2b](https://github.com/GeorgeV220/cosmicjars/commit/0f51b2bba3b965cf3120f5b502bc4feec7f7ea82))

# [1.3.0](https://github.com/GeorgeV220/cosmicjars/compare/v1.2.0...v1.3.0) (2024-07-18)


### Features

* **providers:** Add PandaSpigotProvider implementation ([0a64618](https://github.com/GeorgeV220/cosmicjars/commit/0a646180884cb53436b61acf009be584a0139f72))
* **utils:** Add method to copy InputStream to file ([bd559b8](https://github.com/GeorgeV220/cosmicjars/commit/bd559b88f96f7ee070f6980edbb89c611097f513))

# [1.2.0](https://github.com/GeorgeV220/cosmicjars/compare/v1.1.0...v1.2.0) (2024-07-08)


### Bug Fixes

* Rename centroJars.yml to cosmicJars.yml ([4636fb9](https://github.com/GeorgeV220/cosmicjars/commit/4636fb9ddb55b60d18a22ccf40178a69bd05453a))


### Features

* Add logging configuration and custom formatter ([701ec67](https://github.com/GeorgeV220/cosmicjars/commit/701ec673b41c0ffe2316782d0e911bbcfb8a868c))
* **config:** Rename config file name to cosmicJars.yml ([51279c6](https://github.com/GeorgeV220/cosmicjars/commit/51279c60df7dc9aaf2a5cee64fbc791f6bbd59bc))
* Update server configuration on config changes ([f04e535](https://github.com/GeorgeV220/cosmicjars/commit/f04e535c108ab544d6aa7b6811d9d6c2e2984bf6))

# [1.1.0](https://github.com/GeorgeV220/cosmicjars/compare/v1.0.0...v1.1.0) (2024-05-14)


### Bug Fixes

* handle console output in CosmicJarsFrame ([594bf2d](https://github.com/GeorgeV220/cosmicjars/commit/594bf2d4cc3ce778027d467b087a9eb83310132f))


### Features

* Add ConsoleOutputHandler ([0b1438c](https://github.com/GeorgeV220/cosmicjars/commit/0b1438cb1d120c2975f8a55284998788307f65b4))
* **gui:** add SmartScroller class ([fd138c3](https://github.com/GeorgeV220/cosmicjars/commit/fd138c3b9e40f669b0be775a57cd4f1d708fc3c0))
* **gui:** add SmartScroller for console text pane ([6f4bf4f](https://github.com/GeorgeV220/cosmicjars/commit/6f4bf4f65c1b08238dc72ceb213aeac4dfd0b23c))
* **provider:** add FabricProvider implementation ([c53d2ca](https://github.com/GeorgeV220/cosmicjars/commit/c53d2ca67fdc0b4729541e549ae8cc52c4005efa))

# 1.0.0 (2024-05-04)


### Features

* Add BungeeCord provider implementation ([5b6a62d](https://github.com/GeorgeV220/cosmicjars/commit/5b6a62d0f9a81e495e78ed2535d98db2a373a7e5))
* Add command line argument parsing ([0dc3a90](https://github.com/GeorgeV220/cosmicjars/commit/0dc3a901129ace0de46bec906467d739c6df1c36))
* add Gson dependency to build.gradle ([ab7b1d5](https://github.com/GeorgeV220/cosmicjars/commit/ab7b1d5541e32c6ceddb2d54862a7eb6eb61cba1))
* Add logging for received and starting commands ([b4f1eca](https://github.com/GeorgeV220/cosmicjars/commit/b4f1eca025249734c24b5761ce0f83aa580fe186))
* Add logging for selected server type, implementation, and version ([9715bd2](https://github.com/GeorgeV220/cosmicjars/commit/9715bd291636b678853eab70e81620caf4e8bdfd))
* Add MinecraftServer helper class ([e6ecee3](https://github.com/GeorgeV220/cosmicjars/commit/e6ecee3ba0101e334cbc5d9a63eb4ae7891d4d1c))
* Add new dependencies for project ([5f0fa35](https://github.com/GeorgeV220/cosmicjars/commit/5f0fa3556812ec21dcb759886eb06ab64760b46c))
* Add Provider and Utils classes ([72ecc8b](https://github.com/GeorgeV220/cosmicjars/commit/72ecc8ba148ab8da690112de198fa1f5f71d209c))
* Add support for multiple server implementations ([5587ecc](https://github.com/GeorgeV220/cosmicjars/commit/5587ecc4b6ed18f58d2b206f8a6152f06832c154))
* **build:** add shadow plugin ([20b86a3](https://github.com/GeorgeV220/cosmicjars/commit/20b86a37c20491c9a10624abd61cafea5ca8b429))
* Download libraries instead of shading them in the main jar file ([9860d99](https://github.com/GeorgeV220/cosmicjars/commit/9860d995a521cfe124ce3b7333b4fae41916e4dd))
* **gui:** Add HistoryTextField component ([61fd60a](https://github.com/GeorgeV220/cosmicjars/commit/61fd60a8a154ffb07f0364a637cb32c47dd47e94))
* **GUI:** Added simple GUI mode ([0349929](https://github.com/GeorgeV220/cosmicjars/commit/034992949f71d07e4858b15ba06e05156257a5bb))
* Improve server process handling ([6861ba4](https://github.com/GeorgeV220/cosmicjars/commit/6861ba406b9bc269de18bc50a2792205e81497ec))
* improve user input handling in Main.java ([c045fe7](https://github.com/GeorgeV220/cosmicjars/commit/c045fe71bad7160971471507e168dc89e9a75c4b))
* **logging:** update log4j2 pattern layout ([db120c7](https://github.com/GeorgeV220/cosmicjars/commit/db120c7a16a8893cc6c12bfabc2fe6055c631e89))
* **Main:** Add getJavaExecutable method ([c349dbe](https://github.com/GeorgeV220/cosmicjars/commit/c349dbe795d6b3daf9b2300a316ce1f3d8fedd81))
* **platform:** add Platform class to determine current OS ([6964349](https://github.com/GeorgeV220/cosmicjars/commit/69643496ec6d87545043c58d64d16ff676b5e369))
* **provider:** Add CentroJarProvider implementation ([95f8d0b](https://github.com/GeorgeV220/cosmicjars/commit/95f8d0b9ef0783a51437f4bd9286ee5908b539b6))
* **providers:** Add logic to check for existing jar files before download ([a9cf66b](https://github.com/GeorgeV220/cosmicjars/commit/a9cf66be694873d63de5ee181cc4e5ea93502b8f))
* **providers:** Add MohistInfo and MohistProvider classes ([6a88b96](https://github.com/GeorgeV220/cosmicjars/commit/6a88b96211fb40654a4324eea85b50463b0bc890))
* **providers:** Add PaperInfo and PaperProvider classes ([219ce5f](https://github.com/GeorgeV220/cosmicjars/commit/219ce5fa15842a4011a300bf92393556bafb0346))
* **providers:** Add PurpurInfo and PurpurProvider classes ([94ad675](https://github.com/GeorgeV220/cosmicjars/commit/94ad675d0011b1b1895f97abb8a349314e4b1231))
* **providers:** Implement downloadJar method in Provider subclasses ([bdac72b](https://github.com/GeorgeV220/cosmicjars/commit/bdac72bfcb1bcb6e0ec11ec9111defc0e37aab09))
* **README:** Add CosmicJars README.md ([1aebc9f](https://github.com/GeorgeV220/cosmicjars/commit/1aebc9f9fca3a43cd2a0cef033d5a2f8291db6d3))
* Refactor command creation in Main.java ([0aa0298](https://github.com/GeorgeV220/cosmicjars/commit/0aa029825d3ae5c877fcdfc0278ed424b262d8d0))
* Rename CentroJars to CosmicJars ([eb87ce2](https://github.com/GeorgeV220/cosmicjars/commit/eb87ce27e37a6ba65afe3b856c17ab63e763de1a))
* **server:** Improve process handling ([2ca9bca](https://github.com/GeorgeV220/cosmicjars/commit/2ca9bca6f55b832426fb7e6efe61b8cdabb74677))
* Set FlatGitHubDarkIJTheme as default look and feel ([80cb7fb](https://github.com/GeorgeV220/cosmicjars/commit/80cb7fbf7ed57b71a7805cff3ae3fd26f0ec92a3))
* Set Java language version to 17 ([d480151](https://github.com/GeorgeV220/cosmicjars/commit/d480151b78a206e55984d6ea81930999c8562412))
* **utilities:** Add logging for Java executable ([bd67f56](https://github.com/GeorgeV220/cosmicjars/commit/bd67f5608091700e1f7a17cf0d40cb6db487bb3d))
* **utilities:** Add permission setting for Java executable ([985260b](https://github.com/GeorgeV220/cosmicjars/commit/985260b544a54d52f9e9df7ac99c43eb94f14317))
* **utilities:** Add support for extracting JDK from tar.gz files ([461b646](https://github.com/GeorgeV220/cosmicjars/commit/461b646bf226abde38757864f5a8ddbc99246b1a))
* **utils:** Add isWindows() method ([b43ad97](https://github.com/GeorgeV220/cosmicjars/commit/b43ad9716590e3df8b585be915a6bc410e4d714b))
* **utils:** Add JDKUtilities class ([18ce968](https://github.com/GeorgeV220/cosmicjars/commit/18ce96854a52d02bf6776f918dfbdc3078b88303))
* **utils:** Improve download progress logging ([acb2f33](https://github.com/GeorgeV220/cosmicjars/commit/acb2f339d82e5102310d70a04c47611a1886d73a))
* **yml:** Replace Properties file with YAML ([cdcb733](https://github.com/GeorgeV220/cosmicjars/commit/cdcb733b606cec9d2517478bd98cbd7fdfc7a733))
