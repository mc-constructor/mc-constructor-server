# MC Constructor
Monorepo that hosts `dandoes_nodesupport`, `dandoes_minigame`, and `dandoes_minigame_codslap` mods

See [mc-constructor/mc-constructor-js](mc-constructor/mc-constructor-js) for the compainion NodeJS minigame server.

## Usage

- Run `./gradlew buildLibs`
- Copy `./build/libs/client` to the Minecraft client's `mods` folder
- Copy `./build/libs/server` to the Minecraft server's `mods` folder
- Start the Minecraft server
- `mc-constructor/mc-constructor-js`: Run `yarn && yarn start`
- Join the server with at least 1 player (more recommended!)
- As an op, run the command `minigame start codslap`