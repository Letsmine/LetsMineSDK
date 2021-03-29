package eu.letsmine.sdk.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

import com.zaxxer.hikari.HikariDataSource;

import eu.letsmine.functional.SQLExceptionConsumer;
import eu.letsmine.functional.SQLExceptionFunction;
import eu.letsmine.functional.SQLExceptionSupplier;
import lombok.Getter;
import lombok.NonNull;

public final class MySQL {
	
	@Getter
    private final HikariDataSource ds;
	
	/**
	 * Create a new MySQL Connection Pool
	 * @param url jdbc:mariadb:// host : port / database
	 * @param user
	 * @param password
	 * @param database
	 * @param errorLogger
	 * @throws ClassNotFoundException 
	 */
	public MySQL(String url, String user, String password) throws ClassNotFoundException {
	    Class.forName("org.mariadb.jdbc.Driver");
		
        this.ds = new HikariDataSource();
        this.ds.setJdbcUrl(url);
        this.ds.setUsername(user);
        this.ds.setPassword(password);

        this.ds.setMinimumIdle(2);
        this.ds.setPoolName("LetsMine-Connection-Pool");
        this.ds.setMaximumPoolSize(10);
        
        this.ds.setDriverClassName("org.mariadb.jdbc.Driver");

        this.ds.addDataSourceProperty("useUnicode", "true");
        this.ds.addDataSourceProperty("characterEncoding", "utf-8");
        this.ds.addDataSourceProperty("rewriteBatchedStatements", "true");

        this.ds.addDataSourceProperty("cachePrepStmts", "true");
        this.ds.addDataSourceProperty("prepStmtCacheSize", "250");
        this.ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
	}
	
	public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
	}
	
	/**
	 * 
	 * @param success
	 * @param errorConsumer <br><code>SQLException</code> if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * <br><code>SQLTimeoutException</code> when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
	 */
	public void accept(@NonNull SQLExceptionConsumer<Connection> success, Consumer<? super SQLException> errorConsumer) {
		try (var connection = getConnection()) {
			success.accept(connection);
		} catch (SQLException e) {
			if (errorConsumer != null) {
				errorConsumer.accept(e);
			}
		}
	}
	
	/**
	 * 
	 * @param success
	 * @throws SQLException if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * <br><code>SQLTimeoutException</code> when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
	 */
	public void acceptUnhandled(@NonNull SQLExceptionConsumer<Connection> success) throws SQLException {
		try (var connection = getConnection()) {
			success.accept(connection);
		}
	}
	
	/**
	 * 
	 * @param <R>
	 * @param success
	 * @param errorConsumer <br><code>SQLException</code> if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * <br><code>SQLTimeoutException</code> when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
	 * @return
	 */
	public <R> R apply(@NonNull SQLExceptionFunction<Connection, R> success, @NonNull Function<? super SQLException, R> errorConsumer) {
		try (var connection = getConnection()) {
			return success.apply(connection);
		} catch (SQLException e) {
			return errorConsumer.apply(e);
		}
	}
	
	/**
	 * 
	 * @param <R>
	 * @param success
	 * @throws SQLException if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * @throws SQLTimeoutException when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
	 * @return
	 */
	public <R> R applyUnhandled(@NonNull SQLExceptionFunction<Connection, R> success) throws SQLException {
		try (var connection = getConnection()) {
			return success.apply(connection);
		}
	}
	
	/**
	 * 
	 * @param sql
	 * @param prepareStatementConsumer
	 * @param resultConsumer
	 * @param errorConsumer <br><code>SQLException</code> if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * <br><code>SQLTimeoutException</code> when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
	 */
	public void accept(@NonNull String sql, SQLExceptionConsumer<PreparedStatement> prepareStatementConsumer, @NonNull SQLExceptionConsumer<ResultSet> resultConsumer, Consumer<? super SQLException> errorConsumer) {
		try (var connection = getConnection()) {
			try (var prepareStatement = connection.prepareStatement(sql)) {
				if (prepareStatementConsumer != null) {
					prepareStatementConsumer.accept(prepareStatement);
				}
				try (var resultSet = prepareStatement.executeQuery()) {
					resultConsumer.accept(resultSet);
				}
			}
		} catch (SQLException e) {
			if (errorConsumer != null) {
				errorConsumer.accept(e);
			}
		}
	}
	
	/**
	 * @param sql
	 * @param prepareStatementConsumer
	 * @param resultConsumer
	 * @throws SQLException if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * @throws SQLTimeoutException when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
	 */
	public void acceptUnhandled(@NonNull String sql, SQLExceptionConsumer<PreparedStatement> prepareStatementConsumer, @NonNull SQLExceptionConsumer<ResultSet> resultConsumer) throws SQLException {
		try (var connection = getConnection()) {
			try (var prepareStatement = connection.prepareStatement(sql)) {
				if (prepareStatementConsumer != null) {
					prepareStatementConsumer.accept(prepareStatement);
				}
				try (var resultSet = prepareStatement.executeQuery()) {
					resultConsumer.accept(resultSet);
				}
			}
		}
	}

	/**
	 * 
	 * @param <R>
	 * @param sql
	 * @param prepareStatementConsumer
	 * @param resultConsumer
	 * @param errorConsumer <br><code>SQLException</code> if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * <br><code>SQLTimeoutException</code> when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
	 * @return
	 */
	public <R> R apply(String sql, SQLExceptionConsumer<PreparedStatement> prepareStatementConsumer, @NonNull SQLExceptionFunction<ResultSet, R> resultConsumer, Function<? super SQLException, R> errorConsumer) {
		try (var connection = getConnection()) {
			try (var prepareStatement = connection.prepareStatement(sql)) {
				if (prepareStatementConsumer != null) {
					prepareStatementConsumer.accept(prepareStatement);
				}
				try (var resultSet = prepareStatement.executeQuery()) {
					return resultConsumer.apply(resultSet);
				}
			}
		} catch (SQLException e) {
			return errorConsumer.apply(e);
		}
	}

	/**
	 * 
	 * @param <R>
	 * @param sql
	 * @param prepareStatementConsumer
	 * @param resultConsumer
	 * @param errorConsumer <br><code>SQLException</code> if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * <br><code>SQLTimeoutException</code> when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
	 * @return
	 */
	public <R> R apply(SQLExceptionSupplier<String> sql, SQLExceptionConsumer<PreparedStatement> prepareStatementConsumer, @NonNull SQLExceptionFunction<ResultSet, R> resultConsumer, Function<? super SQLException, R> errorConsumer) {
		try (var connection = getConnection()) {
			try (var prepareStatement = connection.prepareStatement(sql.get())) {
				if (prepareStatementConsumer != null) {
					prepareStatementConsumer.accept(prepareStatement);
				}
				try (var resultSet = prepareStatement.executeQuery()) {
					return resultConsumer.apply(resultSet);
				}
			}
		} catch (SQLException e) {
			return errorConsumer.apply(e);
		}
	}

	/**
	 * 
	 * @param <R>
	 * @param sql
	 * @param prepareStatementConsumer
	 * @param resultConsumer
	 * @throws SQLException if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * @throws SQLTimeoutException when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
	 * @return R
	 */
	public <R> R applyUnhandled(String sql, SQLExceptionConsumer<PreparedStatement> prepareStatementConsumer, @NonNull SQLExceptionFunction<ResultSet, R> resultConsumer) throws SQLException {
		try (var connection = getConnection()) {
			try (var prepareStatement = connection.prepareStatement(sql)) {
				if (prepareStatementConsumer != null) {
					prepareStatementConsumer.accept(prepareStatement);
				}
				try (var resultSet = prepareStatement.executeQuery()) {
					return resultConsumer.apply(resultSet);
				}
			}
		}
	}

	/**
	 * 
	 * @param <R>
	 * @param sql
	 * @param prepareStatementConsumer
	 * @param resultConsumer
	 * @param errorConsumer <br><code>SQLException</code> if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * <br><code>SQLTimeoutException</code> when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
	 * @return
	 */
	public <R> R apply(String sql, SQLExceptionFunction<PreparedStatement, R> prepareStatementConsumer, Function<? super SQLException, R> errorConsumer) {
		try (var connection = getConnection()) {
			try (var prepareStatement = connection.prepareStatement(sql)) {
				return prepareStatementConsumer.apply(prepareStatement);
			}
		} catch (SQLException e) {
			return errorConsumer.apply(e);
		}
	}

	/**
	 * 
	 * @param <R>
	 * @param sql
	 * @param prepareStatementConsumer
	 * @param resultConsumer
	 * @param errorConsumer <br><code>SQLException</code> if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * <br><code>SQLTimeoutException</code> when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
	 * @return
	 */
	public <R> R apply(SQLExceptionSupplier<String> sql, SQLExceptionFunction<PreparedStatement, R> prepareStatementConsumer, Function<? super SQLException, R> errorConsumer) {
		try (var connection = getConnection()) {
			try (var prepareStatement = connection.prepareStatement(sql.get())) {
				return prepareStatementConsumer.apply(prepareStatement);
			}
		} catch (SQLException e) {
			return errorConsumer.apply(e);
		}
	}

	/**
	 * 
	 * @param <R>
	 * @param sql
	 * @param prepareStatementConsumer
	 * @param resultConsumer
	 * @throws SQLException if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * @throws SQLTimeoutException when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
	 * @return R
	 */
	public <R> R applyUnhandled(String sql, @NonNull SQLExceptionFunction<PreparedStatement, R> prepareStatementConsumer) throws SQLException {
		try (var connection = getConnection()) {
			try (var prepareStatement = connection.prepareStatement(sql)) {
				return prepareStatementConsumer.apply(prepareStatement);
			}
		}
	}

    /**
     * Executes the given SQL statement, which may be an <code>INSERT</code>,
     * <code>UPDATE</code>, or <code>DELETE</code> statement or an
     * SQL statement that returns nothing, such as an SQL DDL statement.
     * @param sql an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
     * <code>DELETE</code>; or an SQL statement that returns nothing,
     * such as a DDL statement.
	 * @param errorConsumer <br><code>SQLException</code> if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * <br><code>SQLTimeoutException</code> when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     *         or (2) 0 for SQL statements that return nothing
	 */
	public int executeUpdate(@NonNull String sql, Consumer<? super SQLException> errorConsumer) {
		try (var connection = getConnection()) {
			try (var statement = connection.createStatement()) {
				return statement.executeUpdate(sql);
			}
		} catch (SQLException e) {
			if (errorConsumer != null) {
				errorConsumer.accept(e);
			}
			return 0;
		}
	}

    /**
     * Executes the given SQL statement, which may be an <code>INSERT</code>,
     * <code>UPDATE</code>, or <code>DELETE</code> statement or an
     * SQL statement that returns nothing, such as an SQL DDL statement.
     * @param sql an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
     * <code>DELETE</code>; or an SQL statement that returns nothing,
     * such as a DDL statement.
     * @param prepareStatementConsumer 
	 * @throws SQLException if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * @throws SQLTimeoutException when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     *         or (2) 0 for SQL statements that return nothing
	 */
	public int executeUpdate(@NonNull String sql, @NonNull SQLExceptionConsumer<PreparedStatement> prepareStatementConsumer, Consumer<? super SQLException> errorConsumer) {
		try (var connection = getConnection()) {
			try (var prepareStatement = connection.prepareStatement(sql)) {
				prepareStatementConsumer.accept(prepareStatement);
				return prepareStatement.executeUpdate();
			}
		} catch (SQLException e) {
			if (errorConsumer != null) {
				errorConsumer.accept(e);
			}
			return 0;
		}
	}

    /**
     * Executes the given SQL statement, which may be an <code>INSERT</code>,
     * <code>UPDATE</code>, or <code>DELETE</code> statement or an
     * SQL statement that returns nothing, such as an SQL DDL statement.
     * @param sql an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
     * <code>DELETE</code>; or an SQL statement that returns nothing,
     * such as a DDL statement.
     * @param prepareStatementConsumer 
	 * @throws SQLException if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * @throws SQLTimeoutException when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     *         or (2) 0 for SQL statements that return nothing
	 */
	public int executeUpdate(@NonNull SQLExceptionSupplier<String> sql, @NonNull SQLExceptionConsumer<PreparedStatement> prepareStatementConsumer, Consumer<? super SQLException> errorConsumer) {
		try (var connection = getConnection()) {
			try (var prepareStatement = connection.prepareStatement(sql.get())) {
				prepareStatementConsumer.accept(prepareStatement);
				return prepareStatement.executeUpdate();
			}
		} catch (SQLException e) {
			if (errorConsumer != null) {
				errorConsumer.accept(e);
			}
			return 0;
		}
	}

    /**
     * Executes the given SQL statement, which may be an <code>INSERT</code>,
     * <code>UPDATE</code>, or <code>DELETE</code> statement or an
     * SQL statement that returns nothing, such as an SQL DDL statement.
     * @param sql an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
     * <code>DELETE</code>; or an SQL statement that returns nothing,
     * such as a DDL statement.
     * @param prepareStatementConsumer 
	 * @throws SQLException if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code>
     * or the SQL statement returns a <code>ResultSet</code> object
     * @throws SQLTimeoutException when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     *         or (2) 0 for SQL statements that return nothing
	 */
	public int executeUpdateUnhandled(@NonNull String sql, @NonNull SQLExceptionConsumer<PreparedStatement> prepareStatementConsumer) throws SQLException {
		try (var connection = getConnection()) {
			try (var prepareStatement = connection.prepareStatement(sql)) {
				prepareStatementConsumer.accept(prepareStatement);
				return prepareStatement.executeUpdate();
			}
		}
	}
}