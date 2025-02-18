To run logs example, run local OpenTelemetry Collector.

OpenTelemetry Collector Config (otel-config.yaml):

- OTLP receiver listens for logs via gRPC on port 4317.
- Batch processor groups logs to reduce network calls.
- Logging exporter sends the logs to stdout at the debug level.

```yaml
receivers:
  otlp:
    protocols:
      grpc:

processors:
  batch:

exporters:
  logging:
    loglevel: debug  # Logs will be printed to stdout

service:
  pipelines:
    logs:
      receivers: [otlp]
      processors: [batch]
      exporters: [logging]
```

Running the Collector with Docker:

```yaml
version: '3'
services:
  otel-collector:
    image: ghcr.io/open-telemetry/opentelemetry-collector:latest
    ports:
      - "4317:4317"  # Exposes OTLP gRPC endpoint
    volumes:
      - ./otel-config.yaml:/etc/otel/config.yaml  # Use the config file from your local directory
    command:
      - "--config=/etc/otel/config.yaml"
```

Run it with:

```bash
docker-compose up
```