package org.duhen.dglyphs;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class MasterTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        syncTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_file), MODE_PRIVATE);

        boolean newState = !prefs.getBoolean("master_allow", false);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("master_allow", newState);

        if (!newState) {
            editor.putBoolean("is_glyph_active", false);
            editor.putBoolean("is_random_active", false);
            updateHardware(0);
        }

        editor.apply();
        syncTile();

        TileService.requestListeningState(this,
                new ComponentName(this, GlyphTileService.class));
        TileService.requestListeningState(this,
                new ComponentName(this, RandomGlyphTileService.class));
    }

    private void syncTile() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_file), MODE_PRIVATE);
        boolean isAllowed = prefs.getBoolean("master_allow", false);
        Tile tile = getQsTile();

        if (tile != null) {
            tile.setState(isAllowed ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.setLabel(getString(R.string.tile_master_label));
            tile.updateTile();
        }
    }

    private void updateHardware(int val) {
        GlyphManager.setRandomEffect(false);
        for (GlyphManager.Glyph g : GlyphManager.Glyph.values()) {
            GlyphManager.setBrightness(g, val);
        }
    }
}