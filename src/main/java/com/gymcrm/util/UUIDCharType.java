package com.gymcrm.util;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class UUIDCharType implements UserType<UUID> {
  @Override
  public int getSqlType() {
    return Types.VARCHAR;
  }

  @Override
  public Class<UUID> returnedClass() {
    return UUID.class;
  }

  @Override
  public boolean equals(UUID x, UUID y) {
    return Objects.equals(x, y);
  }

  @Override
  public int hashCode(UUID x) {
    return x != null ? x.hashCode() : 0;
  }

  @Override
  public UUID nullSafeGet(
      ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
      throws SQLException {
    String uuid = rs.getString(position);
    return uuid != null ? UUID.fromString(uuid) : null;
  }

  @Override
  public void nullSafeSet(
      PreparedStatement st, UUID value, int index, SharedSessionContractImplementor session)
      throws SQLException {
    if (value == null) {
      st.setNull(index, Types.VARCHAR);
    } else {
      st.setString(index, value.toString());
    }
  }

  @Override
  public UUID deepCopy(UUID value) {
    return value;
  }

  @Override
  public boolean isMutable() {
    return false;
  }

  @Override
  public Serializable disassemble(UUID value) {
    return value;
  }

  @Override
  public UUID assemble(Serializable cached, Object owner) {
    return (UUID) cached;
  }

  @Override
  public UUID replace(UUID detached, UUID managed, Object owner) {
    return detached;
  }
}
