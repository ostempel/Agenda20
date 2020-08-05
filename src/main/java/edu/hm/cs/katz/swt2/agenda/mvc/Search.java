package edu.hm.cs.katz.swt2.agenda.mvc;

/**
 * Hilfsklasse die für das Filtern von Tasks nach deren Status.
 */
public class Search {
  
  private SearchEnum searchType = SearchEnum.ALLE;
  private String search;

  public String getSearch() {
    return search;
  }

  public void setSearch(String search) {
    this.search = search;
  }

  public SearchEnum getSearchType() {
    return searchType;
  }

  public void setSearchType(SearchEnum searchType) {
    this.searchType = searchType;
  }
}
