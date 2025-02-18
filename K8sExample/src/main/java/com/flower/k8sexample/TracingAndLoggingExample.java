package com.flower.k8sexample;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;

import java.util.concurrent.TimeUnit;

public class TracingAndLoggingExample {

    public static void main(String[] args) {
        // Set up the FileExporter for traces and logs
        StdoutExporter stdoutExporter = new StdoutExporter();

        // Set up OpenTelemetry with the FileExporter
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(stdoutExporter))
                .build();

        // Create OpenTelemetry SDK instance
        OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setMeterProvider(SdkMeterProvider.builder().build())  // Optional, if you want metrics
                .build();

        // Set OpenTelemetry globally
        GlobalOpenTelemetry.set(openTelemetry);

        // Create a tracer
        Tracer tracer = GlobalOpenTelemetry.getTracer("exampleTracer");

        // Write a log entry
        stdoutExporter.writeLog("Starting the parent operation");

        // Simulate a trace with nested spans and logging
        Span parentSpan = tracer.spanBuilder("ParentOperation").startSpan();
        try {
            // Simulate some work
            TimeUnit.MILLISECONDS.sleep(100);

            // Log some intermediate message
            stdoutExporter.writeLog("Started querying the database");

            // Create a nested span - goToDB
            Span dbSpan = tracer.spanBuilder("goToDB").startSpan();
            try {
                // Simulate DB work
                TimeUnit.MILLISECONDS.sleep(200);
                stdoutExporter.writeLog("Querying the database...");
            } finally {
                dbSpan.end();  // End the database span
            }

            // Create another nested span - transformJSON
            Span transformSpan = tracer.spanBuilder("transformJSON").startSpan();
            try {
                // Simulate transformation work
                TimeUnit.MILLISECONDS.sleep(150);
                stdoutExporter.writeLog("Transforming JSON data...");
            } finally {
                transformSpan.end();  // End the JSON transformation span
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            parentSpan.end();  // End the parent span
            stdoutExporter.writeLog("Completed all operations, traces and logs saved.");
        }

        // Shutdown the tracer provider
        tracerProvider.shutdown();
    }
}
