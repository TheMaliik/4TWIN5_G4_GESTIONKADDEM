# Comprehensive Metrics Monitoring Tutorial

This tutorial guides you through setting up a complete metrics monitoring solution using:
- Node Exporter (for system metrics)
- Spring Boot Actuator (for application metrics)
- Prometheus (for metrics collection)
- Grafana (for visualization)

## Table of Contents

- [Prerequisites](#prerequisites)
- [Part 1: Setting Up Node Exporter](#part-1-setting-up-node-exporter)
- [Part 2: Configuring Spring Boot Metrics](#part-2-configuring-spring-boot-metrics)
- [Part 3: Configuring Prometheus](#part-3-configuring-prometheus)
- [Part 4: Setting Up Grafana](#part-4-setting-up-grafana)
- [Part 5: Creating Dashboards](#part-5-creating-dashboards)
- [Troubleshooting](#troubleshooting)

## Prerequisites

- A Linux server or VM (Ubuntu/CentOS recommended)
- Java 11+ (for Spring Boot application)
- Maven or Gradle (for building Spring Boot app)
- Docker and Docker Compose (optional, for containerized setup)
- Basic knowledge of Java and Spring Boot

## Part 1: Setting Up Node Exporter

Node Exporter collects system metrics like CPU usage, memory, disk space, and network statistics.

### Manual Installation

```bash
# Download Node Exporter
wget https://github.com/prometheus/node_exporter/releases/download/v1.5.0/node_exporter-1.5.0.linux-amd64.tar.gz

# Extract it
tar xvfz node_exporter-1.5.0.linux-amd64.tar.gz

# Move to /usr/local/bin (optional)
sudo mv node_exporter-1.5.0.linux-amd64/node_exporter /usr/local/bin/

# Run Node Exporter
/usr/local/bin/node_exporter &
```

### Setting Up as a System Service

Create a systemd service file:

```bash
sudo nano /etc/systemd/system/node_exporter.service
```

Add the following content:

```ini
[Unit]
Description=Node Exporter
Wants=network-online.target
After=network-online.target

[Service]
User=node_exporter
Group=node_exporter
Type=simple
ExecStart=/usr/local/bin/node_exporter

[Install]
WantedBy=multi-user.target
```

Create a user for Node Exporter:

```bash
sudo useradd -rs /bin/false node_exporter
```

Start and enable the service:

```bash
sudo systemctl daemon-reload
sudo systemctl start node_exporter
sudo systemctl enable node_exporter
```

### Verify Node Exporter

Check if Node Exporter is running:

```bash
curl http://localhost:9100/metrics
```

You should see metrics output like:

```
# HELP node_cpu_seconds_total Seconds the CPUs spent in each mode.
# TYPE node_cpu_seconds_total counter
node_cpu_seconds_total{cpu="0",mode="idle"} 1234.53
...
```

## Part 2: Configuring Spring Boot Metrics

### Step 1: Add Dependencies

#### For Maven (pom.xml):

```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Actuator for exposing metrics -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Micrometer Prometheus Registry -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
</dependencies>
```

#### For Gradle (build.gradle):

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'
}
```

### Step 2: Configure Application Properties

Add the following to your `application.properties` or `application.yml`:

```properties
# Actuator Configuration
management.endpoints.web.exposure.include=health,info,prometheus,metrics
management.endpoint.health.show-details=always
management.endpoint.metrics.enabled=true
management.endpoint.prometheus.enabled=true
management.metrics.export.prometheus.enabled=true

# Custom application name for better identification in Prometheus/Grafana
spring.application.name=my-spring-app
```

### Step 3: Add Custom Metrics (Optional)

Add custom metrics to your application:

```java
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    private final Counter requestCounter;
    private final Timer requestTimer;

    public MyService(MeterRegistry registry) {
        this.requestCounter = Counter.builder("app.requests.total")
                .description("Total number of requests")
                .register(registry);
                
        this.requestTimer = Timer.builder("app.request.duration")
                .description("Request processing time")
                .register(registry);
    }

    public void processRequest() {
        requestCounter.increment();
        
        Timer.Sample sample = Timer.start();
        try {
            // Your business logic here
            performBusinessLogic();
        } finally {
            sample.stop(requestTimer);
        }
    }
    
    private void performBusinessLogic() {
        // Implementation
    }
}
```

### Step 4: Verify Spring Boot Metrics Endpoint

Start your Spring Boot application and check the metrics endpoint:

```bash
curl http://localhost:8080/actuator/prometheus
```

You should see metrics like:

```
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="PS Eden Space",} 4.2088952E7
...
```

## Part 3: Configuring Prometheus

### Step 1: Create Prometheus Configuration

Create a file named `prometheus.yml`:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  # Node Exporter metrics
  - job_name: 'node-exporter'
    static_configs:
      - targets: ['localhost:9100']
        labels:
          instance: 'my-server'

  # Spring Boot application metrics
  - job_name: 'spring-boot'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['localhost:8080']
        labels:
          application: 'my-spring-app'

  # Prometheus self-monitoring
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']
```

### Step 2: Install and Run Prometheus

#### Using Docker:

```bash
docker run -d \
  --name prometheus \
  -p 9090:9090 \
  -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus
```

#### Manual Installation:

```bash
# Download Prometheus
wget https://github.com/prometheus/prometheus/releases/download/v2.37.0/prometheus-2.37.0.linux-amd64.tar.gz

# Extract
tar xvfz prometheus-2.37.0.linux-amd64.tar.gz
cd prometheus-2.37.0.linux-amd64

# Copy your configuration
cp /path/to/your/prometheus.yml .

# Run Prometheus
./prometheus --config.file=prometheus.yml
```

### Step 3: Verify Prometheus Installation

Open Prometheus in your browser:

```
http://localhost:9090
```

Navigate to Status > Targets to verify that all targets are UP.

## Part 4: Setting Up Grafana

### Step 1: Install and Run Grafana

#### Using Docker:

```bash
docker run -d \
  --name grafana \
  -p 3000:3000 \
  grafana/grafana
```

#### Manual Installation:

For Debian/Ubuntu:

```bash
sudo apt-get install -y apt-transport-https software-properties-common
sudo wget -q -O /usr/share/keyrings/grafana.key https://apt.grafana.com/gpg.key

echo "deb [signed-by=/usr/share/keyrings/grafana.key] https://apt.grafana.com stable main" | sudo tee -a /etc/apt/sources.list.d/grafana.list

sudo apt-get update
sudo apt-get install grafana

sudo systemctl start grafana-server
sudo systemctl enable grafana-server
```

### Step 2: Configure Grafana

1. Open Grafana in your browser: `http://localhost:3000`
2. Login with default credentials: admin/admin
3. Change the password when prompted

### Step 3: Add Prometheus Data Source

1. Go to Configuration (gear icon) > Data Sources
2. Click "Add data source"
3. Select "Prometheus"
4. Set URL to `http://localhost:9090` (or your Prometheus server address)
5. Click "Save & Test"

## Part 5: Creating Dashboards

### Option 1: Import Pre-built Dashboards

1. Go to "+" icon > Import
2. Enter a dashboard ID:
   - 1860 (Node Exporter Full)
   - 4701 (Spring Boot 2.1 Statistics)
3. Select your Prometheus data source
4. Click "Import"

### Option 2: Create Custom Dashboards

1. Click "+" > Dashboard > Add new panel
2. Select a visualization type (Graph, Gauge, etc.)
3. Write PromQL queries for your metrics:

#### System Metrics Examples:

```
# CPU Usage
100 - (avg by(instance) (irate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)

# Memory Usage
(node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / node_memory_MemTotal_bytes * 100

# Disk Usage
100 - ((node_filesystem_avail_bytes / node_filesystem_size_bytes) * 100)
```

#### Spring Boot Metrics Examples:

```
# HTTP Request Rate
sum(rate(http_server_requests_seconds_count[1m])) by (instance)

# Response Time (95th percentile)
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (le))

# JVM Memory Usage
sum(jvm_memory_used_bytes{area="heap"}) by (instance) / sum(jvm_memory_max_bytes{area="heap"}) by (instance) * 100
```

4. Save your dashboard

## Troubleshooting

### Node Exporter Issues

- **Port conflicts**: Check if port 9100 is already in use:
  ```bash
  sudo netstat -tulpn | grep 9100
  ```

- **Permission issues**: Make sure the node_exporter user has appropriate permissions:
  ```bash
  sudo chown node_exporter:node_exporter /usr/local/bin/node_exporter
  ```

### Spring Boot Metrics Issues

- **Metrics not showing**: Check if actuator endpoints are correctly exposed:
  ```bash
  curl http://localhost:8080/actuator
  ```

- **404 on /actuator/prometheus**: Verify your application.properties configuration:
  ```properties
  management.endpoints.web.exposure.include=prometheus
  management.endpoint.prometheus.enabled=true
  ```

### Prometheus Issues

- **Target shows as DOWN**: Check if the target is reachable from the Prometheus server:
  ```bash
  curl http://your-target-host:port/metrics
  ```

- **Configuration errors**: Validate your prometheus.yml:
  ```bash
  ./promtool check config prometheus.yml
  ```

### Grafana Issues

- **Cannot connect to Prometheus**: Make sure the Prometheus URL is correct and reachable from Grafana
  
- **No data in dashboards**: Check the time range in the top-right corner of Grafana and verify PromQL queries

## Additional Resources

- [Prometheus Documentation](https://prometheus.io/docs/introduction/overview/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Spring Boot Actuator Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Documentation](https://micrometer.io/docs)

---

This tutorial covers the basics of setting up a comprehensive metrics monitoring system. For production use, consider adding security measures, backup procedures, and high availability configurations. 