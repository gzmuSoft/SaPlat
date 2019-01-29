package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.ZTree;
import io.jboot.admin.service.api.FileProjectService;
import io.jboot.admin.service.api.FilesService;
import io.jboot.admin.service.api.ProjectFileTypeService;
import io.jboot.admin.service.entity.model.FileProject;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.admin.service.entity.model.ProjectFileType;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ProAssReviewService;
import io.jboot.admin.service.entity.model.ProAssReview;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import javax.validation.constraints.Null;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ProAssReviewServiceImpl extends JbootServiceBase<ProAssReview> implements ProAssReviewService {

    /**
     * find file URL by file id
     *
     * @param id
     * @return url
     */
    @Override
    public String findFileURLByFileId(long id) {
        return File.separator + "upload" + File.separator + "test.pdf";
    }

    /**
     * get file tree by project id
     *
     * @param id project
     * @return List<ZTree>
     */
    @Override
    public List<ZTree> findFileTreeByProject(long id) {
        List<ZTree> zTree = new ArrayList<ZTree>();
        FileProjectService fileProjectService = new FileProjectServiceImpl();
        List<FileProject> fileProjects = fileProjectService.findAllByProjectID(id);
        FilesService filesService = new FilesServiceImpl();
        ProjectFileTypeService projectFileTypeService = new ProjectFileTypeServiceImpl();
        for (FileProject item : fileProjects) {

            ProjectFileType projectFileType = projectFileTypeService.findById(item.getFileTypeID());
            Files file = filesService.findById(item.getFileID());
            if(file== null){
                continue;
            }
            //构造叶子节点
            //zTree.add(new ZTree(Long.MAX_VALUE, file.getName(), projectFileType.getParentID(), file.getPath(),file.getId()));
            zTree.add(new ZTree(Long.MAX_VALUE, projectFileType.getName(), projectFileType.getParentID(), file.getPath(),file.getId()));
            do{
                projectFileType = projectFileTypeService.findById(projectFileType.getParentID());
                if(null != projectFileType) {
                    ZTree ztree = new ZTree(projectFileType.getId(), projectFileType.getName(), projectFileType.getParentID());
                    boolean isContains = false;
                    for(ZTree zt : zTree) {
                        if(zTrreEq(zt,ztree)){
                            isContains = true;
                            break;
                        }
                    }
                    if(!isContains){
                        zTree.add(ztree);
                    }
                }
            }while(null != projectFileType);
        }
        return zTree;
    }

    private boolean zTrreEq(ZTree zt1,ZTree zt2){
        if(null == zt2){
            return false;
        }

        if(!zt1.getId().equals(zt2.getId())){
            return false;
        }

        if(!zt1.getpId().equals(zt2.getpId())){
            return false;
        }

        if(null !=zt1.getName() &&(!zt1.getName().equals(zt2.getName()))){
            return false;
        }else if(null == zt1.getName() && null != zt2.getName()){
            return false;
        }

        if(null !=zt1.getTitle() &&(!zt1.getTitle().equals(zt2.getTitle()))){
            return false;
        }else if(null == zt1.getTitle() && null != zt2.getTitle()){
            return false;
        }
        return true;
    }



    /**
     * find model by fileId and projectId
     *
     * @param fileId
     * @param projectId
     * @return ProAssReview model
     */
    @Override
    public List<ProAssReview> findByFileIdAndProjectId(long fileId, long projectId) {
        Columns columns = new Columns();
        columns.eq("fileID", fileId);
        columns.eq("projectID", projectId);
        List<ProAssReview> pars = DAO.findListByColumns(columns);
        System.out.print("#############################################start######################################################");
        System.out.print("\n\n" + pars.size() + "\n\n");
        System.out.print("#############################################end######################################################");
        return pars;
    }


    /**
     * find all model
     *
     * @param model 项目阶段
     * @return all <ProAssReview>
     */
    @Override
    public List<ProAssReview> findAll(ProAssReview model) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        if (StrKit.notNull(model.getIsEnable())) {
            columns.eq("isEnable", model.getIsEnable());
        }
        return DAO.findListByColumns(columns);
    }

    @Override
    public Page<ProAssReview> findPage(ProAssReview model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public boolean isExisted(String name) {
        return findByName(name) != null;
    }

    @Override
    public ProAssReview findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

    @Override
    public List<ProAssReview> findListByProjectIDAndCreateUserID(Long projectID, Long createUserID) {
        Columns columns = new Columns();
        columns.eq("projectID", projectID);
        columns.eq("createUserID", createUserID);
        return DAO.findListByColumns(columns, "createTime desc");
    }
}