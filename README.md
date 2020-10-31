# SASSA

> Stands for: StandAlone Seed Searcher Application

This tool is built to be standalone but has some dependencies which are automatically managed by Gradle. The tool is open source so anyone is able to help, learn, adapt etc.
Getting started with the tool is very easy and there is a simple set of instructions on the info tab (your starting tab). You can
also hover over UI elements to get more information.

## Features

> - *Bedrock Mode*
> - **Nether and End Dimension Searching (Structures and Biomes)**
> - Search based on minecraft version (1.16.X - 1.7.10)
> - Search in specific radius from spawn
> - **Search based on how many structures minimum must exist**
> - World Type (Default, Amplified, Large Biomes)
> - Biomes, Biome Sets (OR function), and Structures
> - Randomized Seeds (Or search from a seed range)


![SeedTool Image 1](https://imgur.com/WyPWotg.png)

![SeedTool Image 2](https://imgur.com/zKAjEgN.png)

![SeedTool Image 3](https://imgur.com/ESwTnVB.png)

![SeedTool Image 4](https://imgur.com/S1C6W5C.png)

![SeedTool Image 5](https://imgur.com/o9tsdYy.png)

### Developers

- Built using Intellij and Java 11
- Runs on Java 8 (1.8)
- Using Gradle to build the project. I created a custom task called buildAll which
builds everything from .JAR, .EXE, .ZIP, and .TAR
- If you have a feature to add create a pull request! Help is always nice.

#### Gradle Build Instructions (any OS)

The [`Gradle` wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) was built using `Gradle 6.7`.
You can choose to use either your system's Gradle installation (note: package managers such as `apt` might not have the latest Gradle version available) or the local Gradle wrapper for the build (replace the `gradle` commands below with `./gradlew` or `gradlew.bat`).

From the project root, run:

```bash
# build the project
gradle build

# run Sassa
gradle run
```

### Content Creators

If you are a content creator and would like to make a video and showcase the tool please feel free.
Just some information regarding about myself and the plans of the tool.

- Its pronounced Zods - mar (Zodz - mar)
- I am a guy so saying *he* is totally fine.

As for future plans please check out the projects page where I am trying to keep up to date with what
I am working on also future plans and ideas. (Nothing is set in stone but there are the plans).

Also if you are going to make a video or content in anyway, I am always looking for developers who
are interested in helping or people who might know different methods of doing the searching and can help
improve things like stability, reliability, etc. So any form of shoutout, kinda of like a reminder to anyone
who is technically inclined would be a great help!

## Credit

#### Dependencies

https://code.google.com/archive/p/json-simple/ <br />
https://github.com/KaptainWutax/FeatureUtils <br />
https://github.com/KaptainWutax/SeedUtils <br />
https://github.com/KaptainWutax/BiomeUtils <br />


#### User Credits

KaptainWutax - Code optimizations and Utils dependencies

scudobuio - for the initial search function

fraffugooby - Updating dependencies

YourCoalAlt - Updating to 1.14.3

PaulW - Adding Seed Range Searching

RedSparrow - Optimization and Bug fixes

#### Contact

Discord: Zodsmar#0877

Email: zodsmar@gmail.com

## Donate

Building this tool takes time. If you love the tool and would like to donate I always appreciate it!

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=W9E3YQAKQWC34&currency_code=CAD&source=url)
