# Warsmash: Mod Engine
This is a Warcraft III modding project that aspires to eventually make Warcraft III modding easier by providing a replacement component for the game program that can be more easily modified while still requiring the Blizzard Warcraft III game and assets to be installed on the computer only from Blizzard. In short, buy Warcraft III from Blizzard, and then you can begin to use this mod engine with it.

## Gameplay Example
[![GAMEPLAY VIDEO](http://img.youtube.com/vi/2YDPQW7uyQ8/0.jpg)](http://www.youtube.com/watch?v=2YDPQW7uyQ8)

## Discord
https://discord.com/invite/ucjftZ7x7H

## How to Install
1. Clone the repo
2. Open the repo as a Gradle Project with your Java IDE of choice (IntelliJ seems to be the easiest to get working)
3. Run the Gradle target called "run"

## How to Build
1. Clone the repo
2. Run ./gradlew desktop:dist
3. Find "./desktop/build/libs/desktop-1.0.jar"
4. Open this JAR with 7zip
5. Remove the duplicate "META-INF/services/javax.imageio.spi.ImageReaderSpi" files that share the same name located in META-INF so that only the BLP related file is present
6. Save the JAR and exit 7zip
*(This process will hopefully become easier in the future)*
   
## How to run/debug in IDE
1. Use a working directory (e. g. the project root directory) to run Warsmash from the IDE.
1. Copy [warsmash.ini](./core/assets/warsmash.ini) file into the working directory.
2. Adapt all the file paths in section`[DataSources]` to your local Warcraft installation or other data sources and make sure that they all do exist and add the project directories [.\core\assets\resources](.\core\assets\resources) and [.\resources](.\resources) are included since they contain required files.
All relative paths must work for your working directory:

````
[DataSources]
Count=8
// Reforged
Type00=CASC
Path00="C:\Program Files (x86)\Warcraft III"
Prefixes00=war3.w3mod,war3.w3mod\_deprecated.w3mod,war3.w3mod\_locales\enus.w3mod
// Warcraft III: Reign of Chaos
Type01=MPQ
Path01="D:\Warcraft III\war3.mpq"
// Warcraft III: The Frozen Throne
Type02=MPQ
Path02="D:\Warcraft III\War3x.mpq"
// Warcraft III: The Frozen Throne Language
Type03=MPQ
Path03="D:\Warcraft III\War3xlocal.mpq"
// Warcraft III: The Frozen Throne Patch
Type04=MPQ
Path04="D:\Warcraft III\War3Patch.mpq"
// Warsmash
Type05=Folder
Path05=".\core\assets\"
Type06=Folder
Path06=".\resources\"
Type07=Folder
Path07="."
````

4. Run/debug the method `com.etheller.warsmash.desktop.DesktopLauncher.main` with the options `-nolog -window -loadfile <path to your .w3x file>` from your IDE to load your custom map file in windowed mode and print the logs into the console instead of a log file.
   If the path to your .w3x file is relative, it has to be found from your working directory.

## Background and History
My current codebase is running on Java 8 and the LibGDX game engine coupled with the port of the mdx-m3-viewer's engine. It contains:
- A transcription of only the relevant portions of this WebGL viewer necessary to load MDX models and W3X map data, and to display the models https://github.com/flowtsohg/mdx-m3-viewer
  - I changed several things about the viewer as of the time of writing, such as a naive implementation of Light objects that will support the Day/Night cycle of the game as well as support point lights for torches and other game objects, however there is a memory leak in my Light system so that it will cause progressively increasing lag on slower computers
  - After the development of this project began, there was code sharing between the MDLX parser in this repo and the one in the "conflictfixes" branch of ReteraModelStudio. Other users (tw1lac and flowtsohg) made contributions to the MDLX parser while it was sitting in that repo, and then I copied it back to this repo, so at this point this repo is including a few changes that were made by those users, but in an undocumented way. Hopefully I will clean this up and get the MDLX parser split from both of these projects off to its own repo. However, credits and thanks to them for their changes to that code (I think it was mostly flowtsohg)! My lack of the git history for those changes in this repo is not an attempt to remove citation/credits for their work, but is rather the result of laziness.
- A transcription of the HiveWE terrain rendering systems from this project, then modified to interface nicely with the ported viewers MDX rendering https://github.com/stijnherfst/HiveWE
  - I changed the orientation of the cliff meshes. Mine are rotated 90 degrees from HiveWE in order to more accurately reproduce the likeness of what we observe in the Warcraft III World Editor. At times, I also tinkered with the Ramp logic and at this point mine is not 1:1 the same as HiveWE, but there are still many issues with mine.
- The shaders GLSL code from both of the above projects, originally copied in almost their exact form but with a few necessary changes. As a result, there are still bizarre disparities in the GL Shader Language version used for different things within this same project
- Graphical Enhancements to viewer from this fork of it: https://github.com/d07RiV/wc3data
(By copying the above repo, I was able to add support for water waves, shadow on the edge of the map, shadow under buildings, UberSplat under buildings, the little shadow picture under units, and some earlier drafts of the unit selection mouse intersect code that I mostly replaced at this point. I had to heavily modify the Splat logic to make them mobile as this repo at the time of copying was only able to create the shadow in a static location and upload the single location to the graphics card. Theoretically I have found cases for Ramps in map terrain that are handled properly by this repo and not by HiveWE terrain rendering nor by my copy of it, but I never prioritized investigating those ramp problems further)
- I use Java-based blp-iio-plugin so I didnt need to bother porting the BLP parser from the model viewer BUT this one I am using has some issue on campaign art textures where the alpha isnt loaded, so NightElfCampaign3D_exp for example has render artifacts around palm leaves https://github.com/DrSuperGood/blp-iio-plugin
- I use an MPQ parser by the same guy as the above, DrSuperGood, but he didnt put it on github so I just included the sourcecode in the repo for now
- For loading Unit Data, I have two SLK parsers and two INI parsers which is gross. One set is copied from ReteraModelStudio and the other is transcribed from viewer. I am mostly using the ReteraModelStudio one where possible because I wrote it not as a copy of anything else from my own intuition years ago and the API interfaces better with my high level unit data API that I copied from ReteraModelStudio (https://github.com/Retera/ReterasModelStudio)
- For loading map changeset unit data (Object Editor) I am using a transcription of the parsers written by PitzerMike for Grimoire/Widgetizer. Somebody linked me some ancient pastebin on the Hive at some point that included this C++ code so I wrote a Java port in my model editor repo and copied it to this repo and cleaned it up a bit
- For loading TGA image files such as building pathing, I use the same TGA parser included in ReteraModelStudio codebase. It was written by OgerLord long ago https://github.com/OgerLord/WcDataLibrary/blob/master/src/de/wc3data/image/TgaFile.java

## Legal Stuff
I have tagged this repository with the MIT license. From my understanding this means that the users are free to take the contents of the repo and try to encrypt it all and sell it to each other. Some day, maybe a user will download this repo and reprogram a modified version that only plays the DotA map and use that as a DotA engine thing that they would sell to others and prevent me from modifying or using their upgrades. In my opinion, that is not very cool -- and I do not have experience playing the DotA map -- but I am setting up the repo here so that it does not stop them from doing that. Also, I am guessing that since MIT license probably allows selling modified versions of the code and stuff, this hopefully would leave the door open that Blizzard could download this repo and take stuff out of it and include it in their private Warcraft III game code if they ever needed to. At the time of writing I do not think my repo has anything in particular that Warcraft III Reforged does not have, however, so this is purely hypothetical that I am intending to leave as an open door for the future.
