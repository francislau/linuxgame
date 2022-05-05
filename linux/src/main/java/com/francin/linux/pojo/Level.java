package com.francin.linux.pojo;


import java.sql.Timestamp;

public class Level {

  private long id;
  private String title;
  private String difficulty;
  private long status;
  private java.sql.Timestamp createAt;
  private java.sql.Timestamp updateAt;
  private long createBy;
  private long updateBy;

  public Level() {
  }

  public Level(long id, String title, String difficulty, long status, Timestamp createAt, Timestamp updateAt, long createBy, long updateBy) {
    this.id = id;
    this.title = title;
    this.difficulty = difficulty;
    this.status = status;
    this.createAt = createAt;
    this.updateAt = updateAt;
    this.createBy = createBy;
    this.updateBy = updateBy;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  public String getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(String difficulty) {
    this.difficulty = difficulty;
  }


  public long getStatus() {
    return status;
  }

  public void setStatus(long status) {
    this.status = status;
  }


  public java.sql.Timestamp getCreateAt() {
    return createAt;
  }

  public void setCreateAt(java.sql.Timestamp createAt) {
    this.createAt = createAt;
  }


  public java.sql.Timestamp getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(java.sql.Timestamp updateAt) {
    this.updateAt = updateAt;
  }


  public long getCreateBy() {
    return createBy;
  }

  public void setCreateBy(long createBy) {
    this.createBy = createBy;
  }


  public long getUpdateBy() {
    return updateBy;
  }

  public void setUpdateBy(long updateBy) {
    this.updateBy = updateBy;
  }

  @Override
  public String toString() {
    return "Level{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", difficulty='" + difficulty + '\'' +
            ", status=" + status +
            ", createAt=" + createAt +
            ", updateAt=" + updateAt +
            ", createBy=" + createBy +
            ", updateBy=" + updateBy +
            '}';
  }
}
