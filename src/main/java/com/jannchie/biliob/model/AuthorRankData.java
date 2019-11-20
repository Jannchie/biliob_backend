package com.jannchie.biliob.model;

/**
 * @author Pan Jianqi
 */
public class AuthorRankData {
    private Long archiveViewRank;
    private Long articleViewRank;
    private Long likeRank;
    private Long fansRank;

    public AuthorRankData(Long archiveViewRank, Long articleViewRank, Long likeRank, Long fansRank) {
        this.archiveViewRank = archiveViewRank;
        this.articleViewRank = articleViewRank;
        this.likeRank = likeRank;
        this.fansRank = fansRank;
    }

    public AuthorRankData() {
    }

    public Long getArchiveViewRank() {
        return archiveViewRank;
    }

    public void setArchiveViewRank(Long archiveViewRank) {
        this.archiveViewRank = archiveViewRank;
    }

    public Long getArticleViewRank() {
        return articleViewRank;
    }

    public void setArticleViewRank(Long articleViewRank) {
        this.articleViewRank = articleViewRank;
    }

    public Long getLikeRank() {
        return likeRank;
    }

    public void setLikeRank(Long likeRank) {
        this.likeRank = likeRank;
    }

    public Long getFansRank() {
        return fansRank;
    }

    public void setFansRank(Long fansRank) {
        this.fansRank = fansRank;
    }
}
