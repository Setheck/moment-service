package com.setheck.momentservice;

import com.fasterxml.jackson.annotation.*;
import io.dropwizard.Configuration;
import io.dropwizard.db.*;

import javax.validation.*;
import javax.validation.constraints.*;


public class MomentServiceConfiguration extends Configuration
{
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory()
    {
        return database;
    }

    public void setDataSourceFactory(DataSourceFactory database) {
        this.database = database;
    }
}
