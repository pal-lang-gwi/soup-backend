# Initial Setup Commands

Assumptions:

- Docker Engine and the Docker Compose plugin are already installed
- DNS for `BACKEND_PUBLIC_HOST` already points to your home server public IP
- TLS certificate files are already prepared
- You will run the stack from `/opt/soup`

Create directories:

```bash
sudo mkdir -p /opt/soup/backend/prompts
sudo mkdir -p /opt/soup/nginx/certs
sudo chown -R "$USER":"$USER" /opt/soup
```

Copy example files:

```bash
cp docs/deployment/home-server/compose.yaml.example /opt/soup/compose.yaml
cp docs/deployment/home-server/.env.example /opt/soup/.env
cp docs/deployment/home-server/nginx/default.conf.template.example /opt/soup/nginx/default.conf.template
cp docs/deployment/home-server/backend/application.yml.example /opt/soup/backend/application.yml
cp docs/deployment/home-server/backend/prompts/news-prompt.txt.example /opt/soup/backend/prompts/news-prompt.txt
```

Place TLS certificate files:

```bash
cp /path/to/fullchain.pem /opt/soup/nginx/certs/fullchain.pem
cp /path/to/privkey.pem /opt/soup/nginx/certs/privkey.pem
chmod 600 /opt/soup/nginx/certs/privkey.pem
```

Edit runtime files:

```bash
nano /opt/soup/.env
nano /opt/soup/nginx/default.conf.template
nano /opt/soup/backend/application.yml
nano /opt/soup/backend/prompts/news-prompt.txt
```

Login to Docker Hub on the server once:

```bash
docker login
```

Validate and start the stack:

```bash
cd /opt/soup
docker compose config --quiet
docker compose pull
docker compose up -d
docker compose ps
```

Inspect logs:

```bash
cd /opt/soup
docker compose logs -f nginx
docker compose logs -f backend
docker compose logs -f worker
```

Health checks:

```bash
curl -fsS https://api.soupspoon.kr/api/v1/health
curl -fsS http://127.0.0.1:8000/health
```

Open firewall ports if you use `ufw`:

```bash
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw status
```

Useful update commands:

```bash
cd /opt/soup
docker compose pull backend worker
docker compose up -d backend worker
docker compose ps
```
