# README.md

The project is setup based on this BULMA [guide](https://bulma.io/documentation/customize/with-webpack/).
It generates the javascript and css bundles for the dashboard website.

- Run a dev server with: `npm run dev`
- Visit at http://localhost:8080
- You can also supply a region which is then located at the top of the table: `http://localhost:8080/?region=Nuernberg` or `http://localhost:8080/?region=Stuttgart`

Build to the *dist* directory with: `npm run prod`

The *dist* directory is located at *../simra-project.github.io*, which is a git submodule repository that is published via GitHub pages.