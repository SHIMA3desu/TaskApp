package jp.techacademy.takafumi.matsushima.taskapp;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by qcq0447 on 2016/12/09.
 */

public class Task extends RealmObject implements Serializable {
    private String title; // タイトル
    private String contents; // 内容
    private String category; // カテゴリ
    private Date date; // 日時

    // id をプライマリーキーとして設定
    @PrimaryKey
    private int id;

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) { this.category = category; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
