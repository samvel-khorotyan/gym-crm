package com.gymcrm.unit.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.util.UUIDCharType;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class UUIDCharTypeTest {

  private UUIDCharType uuidCharType;

  @Mock private ResultSet resultSet;

  @Mock private PreparedStatement preparedStatement;

  @Mock private SharedSessionContractImplementor session;

  @BeforeEach
  void setUp() {
    uuidCharType = new UUIDCharType();
    resultSet = mock(ResultSet.class);
    preparedStatement = mock(PreparedStatement.class);
    session = mock(SharedSessionContractImplementor.class);
  }

  @Test
  void getSqlType_ShouldReturnVarcharType() {
    assertEquals(Types.VARCHAR, uuidCharType.getSqlType());
  }

  @Test
  void returnedClass_ShouldReturnUUIDClass() {
    assertEquals(UUID.class, uuidCharType.returnedClass());
  }

  @Test
  void equals_ShouldReturnTrueForEqualUUIDs() {
    UUID uuid1 = UUID.randomUUID();
    UUID uuid2 = UUID.fromString(uuid1.toString());
    assertTrue(uuidCharType.equals(uuid1, uuid2));
  }

  @Test
  void equals_ShouldReturnFalseForDifferentUUIDs() {
    UUID uuid1 = UUID.randomUUID();
    UUID uuid2 = UUID.randomUUID();
    assertFalse(uuidCharType.equals(uuid1, uuid2));
  }

  @Test
  void hashCode_ShouldReturnHashCodeOfUUID() {
    UUID uuid = UUID.randomUUID();
    assertEquals(uuid.hashCode(), uuidCharType.hashCode(uuid));
  }

  @Test
  void hashCode_ShouldReturnZeroForNullUUID() {
    assertEquals(0, uuidCharType.hashCode(null));
  }

  @Test
  void nullSafeGet_ShouldReturnUUID_WhenValueExists() throws SQLException {
    UUID expectedUUID = UUID.randomUUID();
    when(resultSet.getString(1)).thenReturn(expectedUUID.toString());

    UUID actualUUID = uuidCharType.nullSafeGet(resultSet, 1, session, null);

    assertNotNull(actualUUID);
    assertEquals(expectedUUID, actualUUID);
  }

  @Test
  void nullSafeGet_ShouldReturnNull_WhenValueIsNull() throws SQLException {
    when(resultSet.getString(1)).thenReturn(null);

    UUID actualUUID = uuidCharType.nullSafeGet(resultSet, 1, session, null);

    assertNull(actualUUID);
  }

  @Test
  void nullSafeSet_ShouldSetString_WhenUUIDIsNotNull() throws SQLException {
    UUID uuid = UUID.randomUUID();

    uuidCharType.nullSafeSet(preparedStatement, uuid, 1, session);

    verify(preparedStatement, times(1)).setString(1, uuid.toString());
  }

  @Test
  void nullSafeSet_ShouldSetNull_WhenUUIDIsNull() throws SQLException {
    uuidCharType.nullSafeSet(preparedStatement, null, 1, session);

    verify(preparedStatement, times(1)).setNull(1, Types.VARCHAR);
  }

  @Test
  void deepCopy_ShouldReturnSameUUID() {
    UUID uuid = UUID.randomUUID();
    assertEquals(uuid, uuidCharType.deepCopy(uuid));
  }

  @Test
  void isMutable_ShouldReturnFalse() {
    assertFalse(uuidCharType.isMutable());
  }

  @Test
  void disassemble_ShouldReturnSerializableUUID() {
    UUID uuid = UUID.randomUUID();
    Serializable serialized = uuidCharType.disassemble(uuid);
    assertEquals(uuid, serialized);
  }

  @Test
  void assemble_ShouldReturnUUIDFromSerializable() {
    UUID uuid = UUID.randomUUID();
    UUID assembled = uuidCharType.assemble(uuid, null);
    assertEquals(uuid, assembled);
  }

  @Test
  void replace_ShouldReturnDetachedUUID() {
    UUID uuidDetached = UUID.randomUUID();
    UUID uuidManaged = UUID.randomUUID();
    assertEquals(uuidDetached, uuidCharType.replace(uuidDetached, uuidManaged, null));
  }
}
