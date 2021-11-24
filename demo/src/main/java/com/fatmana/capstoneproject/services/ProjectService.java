package com.fatmana.capstoneproject.services;


import com.fatmana.capstoneproject.domain.Backlog;
import com.fatmana.capstoneproject.domain.Project;
import com.fatmana.capstoneproject.domain.User;
import com.fatmana.capstoneproject.exceptions.ProjectIdException;
import com.fatmana.capstoneproject.repositories.BacklogRepository;
import com.fatmana.capstoneproject.repositories.ProjectRepository;
import com.fatmana.capstoneproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProjectService {

    @Autowired
    //Spring injecting (doing the initializing of the variable) the variable in based on configurations you defined in classes with the @Component annotation
    private ProjectRepository projectRepository;


    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdateProject(Project project, String username){
        try{
            User user = userRepository.findByUsername(username);
            project.setUser(user);
            project.setProjectLeader(user.getUsername());
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());

            if(project.getId()==null){
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            }

            if(project.getId()!=null){
                project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
            }

            return projectRepository.save(project);

        }catch (Exception e){
            throw new ProjectIdException("Project ID '"+project.getProjectIdentifier().toUpperCase()+"' already exists");
        }

    }


    public Project findProjectByIdentifier(String projectId){

        //Only want to return the project if the user looking for it is the owner

        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(project == null){
            throw new ProjectIdException("Project ID '"+projectId+"' does not exist");

        }


        return project;
    }

    public Iterable<Project> findAllProjects(){
        return projectRepository.findAll();
    }


    public void deleteProjectByIdentifier(String projectid){
        Project project = projectRepository.findByProjectIdentifier(projectid.toUpperCase());

        if(project == null){
            throw  new  ProjectIdException("Cannot Project with ID '"+projectid+"'. This project does not exist");
        }

        projectRepository.delete(project);
    }

}