package upa.utils;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleTypes;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import upa.db.exception.QueryException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Daniel Dolejška
 * @since 2019-12-06
 */
public class Query
{
    /**
     * Inserts ORAData into database query at given parameter index.
     *
     * @param query Prepared database query.
     * @param index Query parameter index.
     * @param data  Query parameter data.
     */
    public static void SetORAData(PreparedStatement query, final int index, ORAData data) throws QueryException
    {
        try
        {
            final OraclePreparedStatement oracleQuery = (OraclePreparedStatement) query;
            oracleQuery.setORAData(index, data);
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to insert provided ORAData instance into provided database query.", e);
        }
    }

    /**
     * Gets ORAData from database query at given index.
     *
     * @param resultSet Prepared database query.
     * @param index     Query parameter index.
     * @param factory   ORAData object factory.
     */
    public static ORAData GetORAData(ResultSet resultSet, final int index, ORADataFactory factory) throws QueryException
    {
        try
        {
            final OracleResultSet oracleResultSet = (OracleResultSet) resultSet;
            return oracleResultSet.getORAData(index, factory);
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to get ORAData instance from provided database ResultSet.", e);
        }
    }

    /**
     * Creates and registers database query return parameter at given index.
     * This requires <code>RETURNING xxx INTO ?</code> to be present in provided database query.
     *
     * @param query Database insertion query before execution.
     * @param index Parameter index in query.
     */
    public static void RegisterReturnId(PreparedStatement query, final int index)
    {
        try
        {
            final OraclePreparedStatement oracleQuery = (OraclePreparedStatement) query;
            oracleQuery.registerReturnParameter(index, OracleTypes.NUMBER);
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to register return ID parameter.", e);
        }
    }

    /**
     * Gets database query return parameter's value.
     *
     * @param query Successfully executed database insertion query.
     */
    public static int GetReturnId(PreparedStatement query) throws QueryException
    {
        return Query.GetReturnId(query, 1);
    }

    /**
     * Gets database query return parameter's value at given index.
     *
     * @param query Successfully executed database insertion query.
     * @param index Index of return parameter.
     */
    public static int GetReturnId(PreparedStatement query, final int index) throws QueryException
    {
        try
        {
            final OraclePreparedStatement oracleQuery = (OraclePreparedStatement) query;
            ResultSet resultSet = oracleQuery.getReturnResultSet();
            if (resultSet == null || !resultSet.next())
                throw new QueryException("Failed to get inserted row ID. Return ResultSet is empty.");

            return resultSet.getInt(index);
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to get inserted row ID.", e);
        }
    }
}
