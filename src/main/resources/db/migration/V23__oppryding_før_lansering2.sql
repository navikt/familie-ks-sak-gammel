ALTER TABLE behandling
RENAME journalpostid TO journalpost_id;

alter table behandling alter column journalpost_id set not null;


