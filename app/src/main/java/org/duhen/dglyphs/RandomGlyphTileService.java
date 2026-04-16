package org.duhen.dglyphs;

import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class RandomGlyphTileService extends TileService {

    private SharedPreferences prefs;
    private final SharedPreferences.OnSharedPreferenceChangeListener prefListener =
            (sharedPreferences, key) -> {
                if ("master_allow".equals(key) || "is_glyph_active".equals(key)) {
                    syncTile();
                }
            };

    @Override
    public void onStartListening() {
        super.onStartListening();
        prefs = getSharedPreferences(getString(R.string.pref_file), MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
        syncTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        if (prefs != null) {
            prefs.unregisterOnSharedPreferenceChangeListener(prefListener);
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        if (prefs == null)
            prefs = getSharedPreferences(getString(R.string.pref_file), MODE_PRIVATE);
        if (!prefs.getBoolean("master_allow", false) || prefs.getBoolean("is_glyph_active", false)) {
            syncTile();
            return;
        }
        Tile tile = getQsTile();
        if (tile == null) return;

        boolean currentActive = prefs.getBoolean("is_random_active", false);
        boolean newState = !currentActive;

        prefs.edit().putBoolean("is_random_active", newState).apply();
        GlyphManager.setRandomEffect(newState);
        tile.setState(newState ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    private void syncTile() {
        if (prefs == null)
            prefs = getSharedPreferences(getString(R.string.pref_file), MODE_PRIVATE);

        boolean isAllowed = prefs.getBoolean("master_allow", false);
        boolean isGlyphActive = prefs.getBoolean("is_glyph_active", false);
        boolean isMeActive = prefs.getBoolean("is_random_active", false);

        Tile tile = getQsTile();

        if (tile != null) {
            tile.setLabel(getString(R.string.tile_random_label));

            if (!isAllowed || isGlyphActive) {
                tile.setState(Tile.STATE_UNAVAILABLE);

                if (isMeActive) {
                    prefs.edit().putBoolean("is_random_active", false).apply();
                    GlyphManager.setRandomEffect(false);
                }
            } else {
                tile.setState(isMeActive ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            }
            tile.updateTile();
        }
    }
}