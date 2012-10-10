package mx.ferreyra.persistencia;

import java.io.File;

import mx.ferreyra.utils.Utils;

import android.content.Context;

public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){
        //Find the dir to save cached images
    	//if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
    	//	cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"empresa");
       //else
    	this.cacheDir=context.getCacheDir();
        if(!this.cacheDir.exists())
            this.cacheDir.mkdirs();
    }
    
    public File getFile(String url){

    	String filename = Utils.getSHA1(url);
        File f = new File(this.cacheDir, filename);
        return f;
        
    }
    
    public void clear(){
        File[] files=this.cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }

}