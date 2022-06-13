package server.com;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.nio.file.*;
import java.time.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.*;

public class FileHandler {
    private File file=null;
    private Logger log=LogManager.getLogger(FileHandler.class.getName());

    public FileHandler(){}

    //constructor
    public FileHandler(String filepath) {
        this.file = new File(filepath);
        if (filepath.endsWith(".txt")){
            File fileParent = file.getParentFile();
            if(!fileParent.exists()){
                fileParent.mkdirs();
            }
            if(!file.exists()){
                try{
                    file.createNewFile();
                } catch (Exception e){
                    log.error("File constructor: Create file failed!");
                }
            }
        } else{
            if(!file.exists()){
                file.mkdirs();
            }
        }
        
    }

    public File getFile(){
        return file;
    }

    //append content to txt file
    public void writeTxtAppend(String str){
        FileWriter f1=null;
        try{
            f1=new FileWriter(file,true);
            //System.out.println("Open file successfully!");
            f1.write(str);
            f1.write(System.lineSeparator());
        }catch (Exception e){
            log.error("writeTxtAppend: Open file failed!");
        }finally{
            try{
                f1.close();
            } catch (Exception e){
                log.error("Close file failed!");
            }
        }
    }

    //write content to txt file
    public void writeTxtFile(String str){
        FileWriter f1=null;
        try{
            f1=new FileWriter(file);
            //System.out.println("Open file successfully!");
            if (str!=null){
                f1.write(str);
                f1.write(System.lineSeparator());
            }
        }catch (Exception e){
            log.error("writeTxt: Open file failed!");
        }finally{
            try{
                f1.close();
            } catch (Exception e){
                log.error("Close file failed!");
            }
        }
    }

    public List<String> readTxtFile(){
        FileReader fin=null;
        BufferedReader f1=null;
        List<String> txtFile=new ArrayList<String>();

        try{
            fin=new FileReader(file);
            f1=new BufferedReader(fin);
            String str=null;
            while((str=f1.readLine())!=null){
                txtFile.add(str);
            }
        }catch (Exception e){
            log.error("Open file failed!");
        }finally{
            try{
                f1.close();
                fin.close();
                return txtFile;
            }catch(Exception e1){
                log.error("Close file failed!");
            }
        }
        return null;
    }

    //Get all files in the specified folder
    public List<String> getAllFiles(){
        //if the folder is not exist or is a file, return null
        if(Objects.isNull(file) || file.isFile())
            return null;
        
        File[] childrenFiles = file.listFiles();
        if(Objects.isNull(childrenFiles))
            return null;
        
        List<String> txtFile=new ArrayList<String>();
        for(File childFile: childrenFiles){
            txtFile.add(childFile.getName());
        }
        
        return txtFile;
    }

    //Delete files
    public boolean deleteFile(String filepath) {
        this.file=new File(filepath);
        if (file.isFile() && file.exists()){
            file.delete();
            log.info("Delete successfully: " + filepath);
            return true;
        } else{
            log.error("Delete file failed: File not exists.");
            return false;
        }
    }

    //Delete directory
    public boolean deleteDirectory(String dirpath){
        this.file=new File(dirpath);
        if(!file.exists() || !file.isDirectory()){
            log.error("Delete directory failed: Directory not exists.");
            return false;
        }
        boolean flag=true;
        //Delete files in the directory
        File[] childrenFiles = file.listFiles();
        for(File childFile: childrenFiles){
            if(childFile.isFile()){
                flag=deleteFile(childFile.getAbsolutePath());
                if (!flag){break;}
            } else{
                flag=deleteDirectory(childFile.getAbsolutePath());
                if (!flag){break;}
            }
        }

        if(!flag){
            log.error("Delete directory failed.");
            return false;
        }
        //Delete current folder
        File current=new File(dirpath);
        if(current.delete()){
            log.info("Delete successfully"+ dirpath);
            return true;
        } else{
            log.error("Delete directory failed.");
            return false;
        }
    }

    //turn file into byte array
    public byte[] fileToByteArray() {
        byte[] bytes = new byte[(int)file.length()];
        try{
            FileInputStream fin=new FileInputStream(file);
            fin.read(bytes);
            fin.close();
            return bytes;
        }catch(IOException e){
            log.error("Turn file into byte array failed.");
            return null;
        }
        
    }

    //write byte array to file
    public boolean byteArrayToFile(byte[] bytes){
        try{
            OutputStream fout=new FileOutputStream(file);
            fout.write(bytes);
            fout.close();
            return true;
        } catch (IOException e){
            log.error("Write byte array to file failed");
            return false;
        }
        
    }

    public String fileTime(File _file){
        Path filepath=Paths.get(_file.getAbsolutePath());
        BasicFileAttributes fileAttr=null;
        try{
            fileAttr = Files.readAttributes(filepath, BasicFileAttributes.class);
            
        }catch (IOException e){
            e.printStackTrace();
        }
        long changeTime = fileAttr.lastModifiedTime().toMillis();
        ZonedDateTime t = Instant.ofEpochMilli(changeTime).atZone(ZoneId.of("America/Los_Angeles"));
        String lastModifiedTimeString = DateTimeFormatter.ofPattern("MM/dd/yyyy").format(t);
        return lastModifiedTimeString;
    }

    //Get all files time in the specified folder
    public List<String> getAllFileTimes(){
        //if the folder is not exist or is a file, return null
        if(Objects.isNull(file) || file.isFile())
            return null;
        
        File[] childrenFiles = file.listFiles();
        if(Objects.isNull(childrenFiles))
            return null;
        
        List<String> txtFile=new ArrayList<String>();
        for(File childFile: childrenFiles){
            txtFile.add(fileTime(childFile));
        }
        
        return txtFile;
    }
}
