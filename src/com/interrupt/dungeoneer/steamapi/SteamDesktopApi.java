package com.interrupt.dungeoneer.steamapi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.codedisaster.steamworks.*;
import com.interrupt.api.steam.SteamApiInterface;

public class SteamDesktopApi
        implements SteamApiInterface
{
    protected SteamUGCCallback ugcCallback = new SteamUGCCallback()
    {
        public void onUGCQueryCompleted(SteamUGCQuery query, int numResultsReturned, int totalMatchingResults, boolean isCachedData, SteamResult result) {}

        public void onSubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {}

        public void onUnsubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {}

        public void onRequestUGCDetails(SteamUGCDetails details, SteamResult result) {}

        public void onCreateItem(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {}

        public void onSubmitItemUpdate(boolean needsToAcceptWLA, SteamResult result) {}

        public void onDownloadItemResult(int appID, SteamPublishedFileID publishedFileID, SteamResult result) {}

        public void onUserFavoriteItemsListChanged(SteamPublishedFileID publishedFileID, boolean wasAddRequest, SteamResult result) {}

        public void onSetUserItemVote(SteamPublishedFileID publishedFileID, boolean voteUp, SteamResult result) {}

        public void onGetUserItemVote(SteamPublishedFileID publishedFileID, boolean votedUp, boolean votedDown, boolean voteSkipped, SteamResult result) {}

        public void onStartPlaytimeTracking(SteamResult result) {}

        public void onStopPlaytimeTracking(SteamResult result) {}

        public void onStopPlaytimeTrackingForAllItems(SteamResult result) {}
    };
    protected SteamUserStatsCallback userStatsCallback = new SteamUserStatsCallback()
    {
        public void onUserStatsReceived(long gameId, SteamID steamIDUser, SteamResult result) {}

        public void onUserStatsStored(long gameId, SteamResult result) {}

        public void onUserStatsUnloaded(SteamID steamIDUser) {}

        public void onUserAchievementStored(long gameId, boolean isGroupAchievement, String achievementName, int curProgress, int maxProgress) {}

        public void onLeaderboardFindResult(SteamLeaderboardHandle leaderboard, boolean found) {}

        public void onLeaderboardScoresDownloaded(SteamLeaderboardHandle leaderboard, SteamLeaderboardEntriesHandle entries, int numEntries) {}

        public void onLeaderboardScoreUploaded(boolean success, SteamLeaderboardHandle leaderboard, int score, boolean scoreChanged, int globalRankNew, int globalRankPrevious) {}

        public void onGlobalStatsReceived(long gameId, SteamResult result) {}
    };
    protected SteamUserStats stats = null;

    public boolean init()
    {
        try
        {
            boolean started = SteamAPI.init();
            if (started) {
                this.stats = new SteamUserStats(this.userStatsCallback);
            }
            return started;
        }
        catch (SteamException e)
        {
            Gdx.app.error("SteamApi", "Error starting Steam API", e);
        }
        return false;
    }

    public Array<String> getWorkshopFolders()
    {
        Array<String> folders = new Array();
        if (SteamAPI.isSteamRunning())
        {
            SteamUGC ugc = new SteamUGC(this.ugcCallback);

            int numSubbed = ugc.getNumSubscribedItems();
            if (numSubbed > 0)
            {
                SteamPublishedFileID[] ids = new SteamPublishedFileID[numSubbed];
                ugc.getSubscribedItems(ids);
                for (SteamPublishedFileID id : ids)
                {
                    SteamUGC.ItemInstallInfo installInfo = new SteamUGC.ItemInstallInfo();
                    if (ugc.getItemInstallInfo(id, installInfo)) {
                        folders.add(installInfo.getFolder());
                    }
                }
                ugc.startPlaytimeTracking(ids);
            }
            else
            {
                Gdx.app.log("SteamApi", "No subscribed workshop mods");
            }
        }
        return folders;
    }

    public void runCallbacks()
    {
        if (SteamAPI.isSteamRunning()) {
            SteamAPI.runCallbacks();
        }
    }

    public void achieve(String achievementName)
    {
        if ((SteamAPI.isSteamRunning()) && (this.stats != null)) {
            if (this.stats.setAchievement(achievementName)) {
                Gdx.app.log("SteamApi", "Set achievement: " + achievementName);
            } else {
                Gdx.app.log("SteamApi", "Could not set achievement: " + achievementName);
            }
        }
    }

    public void dispose()
    {
        if (SteamAPI.isSteamRunning())
        {
            SteamUGC ugc = new SteamUGC(this.ugcCallback);
            ugc.stopPlaytimeTrackingForAllItems();
            SteamAPI.shutdown();
        }
    }

    public void uploadToWorkshop(Long workshopId, String modImagePath, String modTitle, String modFolderPath) {}

    public boolean isAvailable()
    {
        return SteamAPI.isSteamRunning();
    }
}
