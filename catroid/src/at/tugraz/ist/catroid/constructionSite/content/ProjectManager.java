/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.constructionSite.content;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectManager extends Observable {
    //private final String DEFAULT_PROJECT_NAME = "defaultProject";

    private Sprite currentSprite;
    private Project project;
    private static ProjectManager instance;
    private Script currentScript;

    private ProjectManager() {
    }

    public static ProjectManager getInstance() {
        if (instance == null) {
            instance = new ProjectManager();
        }
        return instance;
    }

    public boolean loadProject(String projectName, Context context) {
        try {
            project = StorageHandler.getInstance().loadProject(projectName);
            if (project == null) {
				initializeNewProject(context.getString(R.string.default_project_name), context);
            }
            currentSprite = null;
            currentScript = null;
            setChanged();
            notifyObservers();
            return true;
        } catch (Exception e) {
            Utils.displayErrorMessage(context, context.getString(R.string.error_load_project));
            return false;
        }
    }

    public void saveProject(Context context) {
        try {
            if (project == null) {
                return;
            }
            StorageHandler.getInstance().saveProject(project);
        } catch (IOException e) {
            Utils.displayErrorMessage(context, context.getString(R.string.error_save_project));
        }
    }

    public void resetProject(Context context) throws NameNotFoundException {
        project = new Project(context, project.getName());
        currentSprite = null;
        currentScript = null;
        setChanged();
        notifyObservers();
    }

    public void addSprite(Sprite sprite) {
        project.addSprite(sprite);
    }
    
    public void addScript(Script script) {
        currentSprite.getScriptList().add(script);
    }

    public void addBrick(Brick brick) {
        currentScript.addBrick(brick);
        setChanged();
        notifyObservers();
    }

    public void moveBrickUpInList(int position) {
        if (position >= 0 && position < currentScript.getBrickList().size()) {
            currentScript.moveBrickBySteps(currentScript.getBrickList().get(position), -1);
            setChanged();
            notifyObservers();
        }
    }

    public void moveBrickDownInList(int position) {
        if (position >= 0 && position < currentScript.getBrickList().size()) {
            currentScript.moveBrickBySteps(currentScript.getBrickList().get(position), 1);
            setChanged();
            notifyObservers();
        }
    }

    public void initializeNewProject(String projectName, Context context) {
        
        project = new Project(context, projectName);
        currentSprite = null;
        currentScript = null;
        saveProject(context);
        setChanged();
        notifyObservers();
       
    }

    public void setObserver(Observer observer) {
        addObserver(observer);
    }

    public Sprite getCurrentSprite() {
        return currentSprite;
    }
    
    public Project getCurrentProject() {
		return project;
    }

    public Script getCurrentScript() {
        return currentScript;
    }

    /**
     * @return false if project doesn't contain the new sprite, true otherwise
     */
	public boolean setCurrentSprite(Sprite sprite) {
		if (sprite == null) { //sometimes we want to set the currentSprite to null because we don't have a currentSprite
			currentSprite = null;
            return true;
        }
		if (project.getSpriteList().contains(sprite)) {
			currentSprite = sprite;
            return true;
        }
        return false;
    }

    /**
     * @return false if currentSprite doesn't contain the new script, true
     *         otherwise
     */
    public boolean setCurrentScript(Script script) {
		if (script == null) {
			currentScript = null;
			return true;
		}
        if (currentSprite.getScriptList().contains(script)) {
            currentScript = script;
            return true;
        }
        return false;
    }
    
    public void setProject(Project project) {
        currentScript = null;
        currentSprite = null;
           
        this.project = project;
    }

    public boolean scriptExists(String scriptName) {
        for (Script script : currentSprite.getScriptList()) {
            if (script.getName().equalsIgnoreCase(scriptName)) {
                return true;
            }
        }
        return false;
    }

    public boolean spriteExists(String spriteName) {
        for (Sprite tempSprite : project.getSpriteList()) {
            if (tempSprite.getName().equalsIgnoreCase(spriteName)) {
                return true;
            }
        }
        return false;
    }
}