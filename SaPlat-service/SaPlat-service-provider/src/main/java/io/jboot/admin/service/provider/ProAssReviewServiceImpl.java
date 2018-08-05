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
            if (null == item.getFileID()) {
                //文件分类，没有对应的文件
                zTree.add(new ZTree(projectFileType.getId(), projectFileType.getName(), projectFileType.getParentID()));
            } else {
                Files file = filesService.findById(item.getFileID());
                zTree.add(new ZTree(Long.MAX_VALUE, file.getName(), projectFileType.getId(), "/upload/test.pdf"));
            }
        }
        return zTree;
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
}