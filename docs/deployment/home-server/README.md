# Home Server Deployment

This directory contains example files for running `soup-backend` and `soup-worker`
on a home server with `docker compose` and `nginx`.

Recommended server layout:

```text
/opt/soup
├── compose.yaml
├── .env
├── nginx
│   ├── certs
│   │   ├── fullchain.pem
│   │   └── privkey.pem
│   └── default.conf.template
└── backend
    ├── application.yml
    └── prompts
        └── news-prompt.txt
```

Use these files as templates:

- `compose.yaml.example` -> `/opt/soup/compose.yaml`
- `.env.example` -> `/opt/soup/.env`
- `nginx/default.conf.template.example` -> `/opt/soup/nginx/default.conf.template`
- `backend/application.yml.example` -> `/opt/soup/backend/application.yml`
- `backend/prompts/news-prompt.txt.example` -> `/opt/soup/backend/prompts/news-prompt.txt`
- `SETUP_COMMANDS.md` -> initial server setup checklist and commands

Basic usage:

```bash
cd /opt/soup
docker compose pull
docker compose up -d
docker compose ps
```

With the current GitHub Actions setup:

- backend deploy expects the service name `backend`
- worker deploy expects the service name `worker`
- deploy workflows override image tags by exporting `BACKEND_IMAGE_TAG` or
  `WORKER_IMAGE_TAG` before running `docker compose`

If you change service names in `compose.yaml`, also change the default
`service_name` input in the deploy workflows.

Recommended exposure model:

- `nginx` publishes `80` and `443`
- `backend` stays internal to the compose network
- `worker` is bound to `127.0.0.1` only for local inspection

Before starting nginx, make sure your DNS record already points to the home
server public IP and your TLS certificate files are ready.

Recommended domains for this project:

- frontend: `https://soupspoon.kr`
- backend API: `https://api.soupspoon.kr`
- health check: `https://api.soupspoon.kr/api/v1/health`
