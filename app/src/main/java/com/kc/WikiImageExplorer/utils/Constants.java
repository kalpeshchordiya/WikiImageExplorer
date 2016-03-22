package com.kc.WikiImageExplorer.utils;

/**
 * Created by kalpesh.chordiya on 3/3/16.
 */
public class Constants {
    public static int ERROR_CODE = 100;
    public static int THUMBNAIL_SIZE = 200;
    public static int NO_OF_PAGES = 50;
    public static String WIKI_IMAGE_SEARCH_URL = "https://en.wikipedia.org/w/api" +
            ".php?action=query&prop=pageimages&format=json&piprop=thumbnail&pilimit=50&generator" +
            "=prefixsearch";
    public static String WIKI_READ_URL_WITH_PAGE_ID = "https://en.wikipedia.org/w/index.php?curid=";
    public static String WIKI_PAGE_RETAINER_FRAGMENT = "RetainerFragment";
    public static String WIKI_PAGE_URL = "LaunchUrl";
    public static String WIKI_PAGE_TITLE = "PageTitle";
    public static String WIKI_PAGE_THUMBNAIL = "Thumbnail";
    public static String WIKI_PAGE_POS = "PagePos";
}
