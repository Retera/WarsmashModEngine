package com.etheller.warsmash;

import java.util.Arrays;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.etheller.warsmash.datasources.CompoundDataSourceDescriptor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.DataSourceDescriptor;
import com.etheller.warsmash.datasources.FolderDataSourceDescriptor;
import com.etheller.warsmash.util.ImageUtils;

public class WarsmashTestMyTextureGame extends ApplicationAdapter {

	private DataSource codebase;
	private Texture texture;
	private SpriteBatch batch;

	@Override
	public void create() {

		final FolderDataSourceDescriptor war3mpq = new FolderDataSourceDescriptor("E:\\Backups\\Warcraft\\Data\\127");
		final FolderDataSourceDescriptor testingFolder = new FolderDataSourceDescriptor(
				"D:\\NEEDS_ORGANIZING\\MPQBuild\\Test");
		final FolderDataSourceDescriptor currentFolder = new FolderDataSourceDescriptor(".");
		this.codebase = new CompoundDataSourceDescriptor(
				Arrays.<DataSourceDescriptor>asList(war3mpq, testingFolder, currentFolder)).createDataSource();

		this.texture = ImageUtils.getAnyExtensionTexture(this.codebase, "Textures\\Dust3x.blp");
		Gdx.gl.glClearColor(0, 0, 0, 1);
		this.batch = new SpriteBatch();
		this.batch.enableBlending();
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		this.batch.begin();
		this.batch.draw(this.texture, 20, 20, 256, 256);
		this.batch.end();
	}

}
