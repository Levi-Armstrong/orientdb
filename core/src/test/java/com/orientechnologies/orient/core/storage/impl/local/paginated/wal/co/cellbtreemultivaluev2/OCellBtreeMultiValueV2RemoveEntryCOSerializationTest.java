package com.orientechnologies.orient.core.storage.impl.local.paginated.wal.co.cellbtreemultivaluev2;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.OOperationUnitId;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class OCellBtreeMultiValueV2RemoveEntryCOSerializationTest {
  @Test
  public void testSerialization() {
    OOperationUnitId operationUnitId = OOperationUnitId.generateId();

    final int indexId = 12;
    final byte keySerializerId = 34;
    final String encryptionName = "encryption";

    final byte[] key = new byte[12];
    final Random random = new Random();
    random.nextBytes(key);

    final ORID value = new ORecordId(12, 38);

    OCellBtreeMultiValueV2RemoveEntryCO co = new OCellBtreeMultiValueV2RemoveEntryCO(indexId, keySerializerId, encryptionName, key,
        value);
    co.setOperationUnitId(operationUnitId);

    final int size = co.serializedSize();
    final byte[] stream = new byte[size + 1];
    int pos = co.toStream(stream, 1);

    Assert.assertEquals(size + 1, pos);
    OCellBtreeMultiValueV2RemoveEntryCO restoredCO = new OCellBtreeMultiValueV2RemoveEntryCO();
    pos = restoredCO.fromStream(stream, 1);

    Assert.assertEquals(size + 1, pos);
    Assert.assertEquals(operationUnitId, restoredCO.getOperationUnitId());
    Assert.assertEquals(indexId, restoredCO.getIndexId());
    Assert.assertEquals(keySerializerId, restoredCO.getKeySerializerId());
    Assert.assertEquals(encryptionName, restoredCO.getEncryptionName());
    Assert.assertArrayEquals(key, restoredCO.getKey());
    Assert.assertEquals(value, restoredCO.getValue());
  }
}
