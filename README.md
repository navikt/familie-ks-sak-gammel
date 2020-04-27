# familie-ks-sak
Behandling av kontantstøtte søknader

## Produksjonssetting
Appen blir produksjonssatt ved å kjøre `tag.sh` som ligger i `.github`. Dette scriptet tagger den seneste commiten i master med det neste versjonsnummeret, og pusher tagen til github-repositoriet.

Hvis den siste tagen er `v0.5`, vil `tag.sh -M` pushe tagen `v1.0`, og `tag.sh -m` pushe tagen `v0.6`.

Ved push av en tag på formen `v*` vil Github Action-workflowen `Build-Deploy-Prod` trigges, som bygger en ny versjon av appen, lagrer imaget i Github Packages, og deployer appen til prod-fss.

