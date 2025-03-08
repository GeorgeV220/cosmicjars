# CosmicJars

Version: 1.4.0

CosmicJars is a powerful tool crafted to simplify the management of your Minecraft server by seamlessly handling the
download and update process of server jars. It ensures that your server software stays current with the latest versions
and builds, supporting popular variants like Paper, Folia, Purpur, Mohist, and Banner.

## Features

- **Automated Updates**: Bid farewell to manual updates. CosmicJars automates the process, guaranteeing your server runs
  on the most recent version or build without your intervention.
- **Versatile Compatibility**: Supporting a variety of server variants including Paper, Folia, Purpur, Mohist, Banner,
  and others, CosmicJars ensures compatibility with your preferred Minecraft environment.
- **Intelligent API Integration**: CosmicJars intelligently switches to utilizing the CentroJars API if your selected
  variant is not directly supported, ensuring seamless updates regardless of the chosen server variant.
- **User-Centric Design**: CosmicJars is designed with ease of use in mind. Simply download the `cosmicjars.jar` file
  and execute it as a server using a command like `java -Xmx2G -jar cosmicjars.jar --nogui`. It automatically downloads
  the latest server jar and provides the console window directly to your Minecraft server or proxy for immediate use.

## Installation

1. Download the latest release of CosmicJars from the [Releases page](https://github.com/GeorgeV220/CosmicJars/releases)
   on GitHub.
2. Run CosmicJars as a server using the following command:
   ```sh
   java -Xmx2G -jar cosmicjars.jar --nogui
   ```
   You can change the JVM arguments to your liking.

## Building from Source

To build CosmicJars from source using Gradle:

1. Open a terminal or command prompt.
2. Clone the repository
3. Navigate to the project directory.
4. Run the following Gradle command:
    - On Unix-like systems (Linux, macOS):
      ```sh
      ./gradlew clean build shadowJar
      ```
    - On Windows:
      ```sh
      ./gradlew.bat clean build shadowJar
      ```
5. Once the build completes, you can find the generated `cosmicjars.jar` file in the `./build/libs/` directory.

## Support

Should you encounter any challenges or have inquiries regarding CosmicJars, don't hesitate
to [raise an issue](https://github.com/GeorgeV220/CosmicJars/issues) on the GitHub repository. Our team is committed to
providing prompt assistance.

## Contributing

CosmicJars welcomes contributions from the community. If you're interested in enhancing the functionality or fixing
bugs, feel free to fork the repository, make your modifications, and submit a pull request.

## License

This project is licensed under the MIT License. For more information, refer to the [LICENSE](LICENSE) file.

## Acknowledgments

[![JetBrains](https://www.jetbrains.com/company/brand/img/jetbrains_logo.png)](https://jb.gg/OpenSourceSupport)

CosmicJars is grateful for the support of JetBrains. Their tools have been instrumental in the development of this
project, and we appreciate their ongoing support.

## Disclaimer

CosmicJars utilizes the public REST API provided by CentroJars for accessing Minecraft server jar information. We are
not affiliated with CentroJars or CentralNodes. Any references to CentroJars are solely for the purpose of utilizing
their API for server jar management.

---

Experience the convenience of Minecraft server management with CosmicJars. Let it handle the heavy lifting of updates
while you focus on enjoying your Minecraft adventures!