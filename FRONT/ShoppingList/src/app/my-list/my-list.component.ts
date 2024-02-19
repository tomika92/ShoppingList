import { HttpClient, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Lists } from '../../models/lists.model';
import { myUsers } from '../../models/myUsers.model';

@Component({
  selector: 'app-my-list',
  templateUrl: './my-list.component.html',
  styleUrls: ['./my-list.component.css']
})
export class MyListComponent implements OnInit {

  userLists: Lists[] = [];
  userRole: string;

  constructor(private router: Router, private http: HttpClient, private route: ActivatedRoute){}

  logoutUser(){
    let Id = this.getId();
    const baseUrl = `http://localhost:8080/logout/${Id}`;
    this.http.get(baseUrl).subscribe(
      (respose: any) => {
        this.router.navigate(['']);
      },
      (error) => {
        console.error("Error during logout", error);
      }
    )
  }

  ngOnInit(): void{
    let Id = this.getId();
    const baseUrl = `http://localhost:8080/getUserLists/${Id}`;
    this.http.get<Lists[]>(baseUrl).subscribe(
      (response: Lists[]) => {
        this.userLists = response.reverse();
        this.getUserRole(Id);
      },
      (error) => {
        console.error("Error during lists downloading", error);
      }
    )
  }

  getUserRole(Id: string){
    const baseUrl = `http://localhost:8080/getUserRole/${Id}`;
    this.http.get<myUsers>(baseUrl).subscribe(
      (response: myUsers) => {
        this.userRole = response.role;
      },
      (error) => {
        if(error.status === 404){
          console.error("Not found", error);
        } 
        else {
          console.error("Error during finding user role", error);
        }
      }
    )
  }

  getId(){
    let Id: any;
    this.route.params.subscribe(params => {
      Id = params['Id'];
    });
    return Id;
  }

  addNewList() {
    let Id = this.getId();
    const listNamePlaceholder = "New list";
    const baseUrl = `http://localhost:8080/addNewList/${Id}`;
    this.http.post<Lists>(baseUrl, listNamePlaceholder).subscribe(
      (response: Lists) => {
        this.userLists.unshift(response);
      },
      (error) => {
        console.error("Error during adding list", error);
      }
    )
  }

  removeList(index: number, id: string){
    const baseUrl = `http://localhost:8080/removeList/${id}`;
    this.http.delete(baseUrl, { observe: 'response' }).subscribe(
      (response: HttpResponse<any>) => {
        this.userLists.splice(index, 1);
      },
      (error) => {
        if(error.status === 404){
          console.error("Not found", error);
        } 
        else {
          console.error("Error during removing list", error);
        }
      }
    )
  }

  updateListName(list: Lists, index: number){
    const listId = list.listsId;
    const newListName = list.listName;
    const baseUrl = `http://localhost:8080/updateListName/${listId}`;
    this.http.put<Lists>(baseUrl, newListName).subscribe(
      (response: Lists) => {
        console.log(response);
        this.userLists[index] = response;
      },
      (error) => {
        if(error.status === 404){
          console.error("Not found", error);
        } 
        else {
          console.error("Error during removing list", error);
        }
      }
    )
  }

  goToDetails(listId: string){
    let userId = this.getId();
    this.router.navigate(['my_list', userId, 'details', listId]);
  } 

  goToAdminPanel(){
    let userId = this.getId();
    this.router.navigate(['my_list', userId, 'adminpanel']);
  }
}
