package com.clickhouse.client;

import com.clickhouse.client.api.insert.InsertResponse;
import com.clickhouse.client.api.insert.InsertSettings;
import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.metadata.TableSchema;
import com.clickhouse.client.generators.InsertDataGenerator;
import com.clickhouse.data.ClickHouseColumn;
import com.clickhouse.data.ClickHouseFormat;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.testng.Assert.assertNotEquals;

public class InsertTests {
    private Client client;

    @BeforeMethod(groups = { "unit" }, enabled = true)
    public void setUp() {
        client = new Client.Builder()
                .addEndpoint("http://localhost:8123")
                .addUsername("default")
                .addPassword("")
                .build();
    }

    @Test(groups = { "unit" }, enabled = true)
    public void registerSimplePOJOs() throws ClickHouseException, SocketException, ExecutionException, InterruptedException {
        System.out.println("registerSimplePOJOs");
        InsertSettings settings = new InsertSettings()
                .setDeduplicationToken("1234567890")
                .setQueryId(String.valueOf(UUID.randomUUID()));

        client.register(Object.class, new TableSchema());
    }

    @Test(groups = { "unit" }, enabled = true)
    public void insertSimplePOJOs() throws ClickHouseException, IOException, ExecutionException, InterruptedException, InvocationTargetException, IllegalAccessException {
        InsertSettings settings = new InsertSettings()
                .setDeduplicationToken("1234567890")
                .setQueryId(String.valueOf(UUID.randomUUID()));

        String table = "simple_pojo_table";
        List<Object> simplePOJOs = InsertDataGenerator.generateSimplePOJOs();
        List<ClickHouseColumn> columns = new ArrayList<>();
        Future<InsertResponse> response = client.insert(table, simplePOJOs, settings);
        assertNotEquals(response.get(), null);
    }

    @Test(groups = { "unit" }, enabled = true)
    public void insertSimpleRowBinary() throws ClickHouseException, SocketException, ExecutionException, InterruptedException {
        InsertSettings settings = new InsertSettings()
                .setFormat(ClickHouseFormat.RowBinary)
                .setDeduplicationToken("1234567890")
                .setQueryId(String.valueOf(UUID.randomUUID()));

        String table = "row_binary_table";
        InputStream dataStream = InsertDataGenerator.generateSimpleRowBinaryData();
        Future<InsertResponse> response = client.insert(table, dataStream, settings);
        assertNotEquals(response.get(), null);
    }
}
