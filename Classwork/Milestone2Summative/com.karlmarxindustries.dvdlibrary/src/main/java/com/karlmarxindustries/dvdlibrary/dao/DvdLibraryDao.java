/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.karlmarxindustries.dvdlibrary.dao;

import com.karlmarxindustries.dvdlibrary.dto.DVD;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author karlmarx
 */
public interface DvdLibraryDao {
    void print(String msg);

    double readDouble(String prompt);

    double readDouble(String prompt, double min, double max);

    float readFloat(String prompt);

    float readFloat(String prompt, float min, float max);

    int readInt(String prompt);

    int readInt(String prompt, int min, int max);

    long readLong(String prompt);

    long readLong(String prompt, long min, long max);

    String readString(String prompt);
    
        List<DVD> getAllDvds() ;
        DVD getDvd(String title) ;
        DVD removeDvd(String title) ;
        DVD addDVD (String title, DVD dvd) ;
        void editDVD(String title, DVD dvd); ///why not working??
    
}