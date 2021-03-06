package com.zjy.utils;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class DbHelper {
    private static DataSource dataSource;

    static {
        // 如果要使用c3p0连接池，则要使用QueryRunner qr = new QueryRunner(dataSource);
        String driver = PropertiesHelper.getInstance().getProperties("db.driverClassName");
        if (!DbUtils.loadDriver(driver)) {
            System.out.println("加载数据库驱动失败！");
        }
    }

    public static int insert(String sql) {
        return update(sql);
    }

    public static int delete(String sql) {
        return update(sql);
    }

    public static int update(String sql) {
        Connection conn = null;
        int count = -1;
        try {
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            // insert方法返回插入行的主键,batch可以批量处理
            count = qr.update(conn, sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return count;
    }

    public static <T> T get(String sql, Class<T> clazz) {

        List<T> list = getList(sql, clazz);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static <T> List<T> getList(String sql, Class<T> clazz, Object... params) {
        Connection conn = null;
        List<T> list = null;
        try {
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            list = (List<T>) qr.query(conn, sql, new BeanListHandler(clazz), params);

            // 使用c3p0连接池
//            conn = getConnection();
//            QueryRunner qr = new QueryRunner(dataSource);
//            r = (List<T>)qr.query(sql, new BeanListHandler(clazz));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return list;
    }


    /**
     * 获取数据库连接，也可以使用c3p0连接池，通过dataSource.getConnection()来获取
     *
     * @return
     */
    public static Connection getConnection() {
        String url = PropertiesHelper.getInstance().getProperties("db.url");
        String user = PropertiesHelper.getInstance().getProperties("db.userName");
        String password = PropertiesHelper.getInstance().getProperties("db.password");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}