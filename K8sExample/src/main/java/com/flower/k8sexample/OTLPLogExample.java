package com.flower.k8sexample;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.api.logs.LogRecordBuilder;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

public class OTLPLogExample {
    private static final OpenTelemetry OPEN_TELEMETRY = initOpenTelemetry();
    private static final Logger LOGGER = OPEN_TELEMETRY.getLogsBridge().get("otel-java-logger");

    private static OpenTelemetry initOpenTelemetry() {
        SdkLoggerProvider loggerProvider = SdkLoggerProvider.builder()
                .addLogRecordProcessor(BatchLogRecordProcessor.builder(
                                OtlpGrpcLogRecordExporter.builder()
                                        .setEndpoint("http://localhost:4317")  // Replace with your OpenTelemetry collector
                                        .build())
                        .build())
                .build();

        return OpenTelemetrySdk.builder()
                .setLoggerProvider(loggerProvider)
                .build();
    }

    public static void main(String[] args) {
        // Example of a structured log with semantic attributes
        logRequest("192.168.1.10", "GET", "/api/data", 200);
    }

    private static void logRequest(String serverAddress, String method, String route, long statusCode) {
        LogRecordBuilder logRecord = LOGGER.logRecordBuilder()
                .setSeverity(Severity.INFO)
                .setBody("Incoming request processed")
                .setAttribute(SemanticAttributes.NET_SOCK_HOST_ADDR, serverAddress)
                .setAttribute(SemanticAttributes.HTTP_METHOD, method)
                .setAttribute(SemanticAttributes.HTTP_ROUTE, route)
                .setAttribute(SemanticAttributes.HTTP_STATUS_CODE, statusCode)
                .setAttribute(AttributeKey.stringKey("customKey"), "customValue");

        logRecord.emit();
    }
}