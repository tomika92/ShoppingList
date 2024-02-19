import { HttpClient, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { myUsers } from '../../models/myUsers.model';

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.css']
})
export class AdminPanelComponent implements OnInit {

  constructor(private http: HttpClient, private route: ActivatedRoute, private location: Location){}
  users: myUsers[] = [];
  loginOrEmail: string;
  tooltipText: string = "red=ADMIN, white=USER";

  goBack(){
    this.location.back();
  }

  ngOnInit(): void{
    const baseUrl = 'http://localhost:8080/getAllUsers';
    this.http.get<myUsers[]>(baseUrl).subscribe(
      (response: myUsers[]) => {
        this.users = response;
      },
      (error) => {
        console.error("Error during users downloading", error);
      }
    )
  }

  removeUser(user: myUsers, index: number){
    const userId = user.userId;
    const myId = this.getId();
    if(userId != myId){
      const baseUrl = `http://localhost:8080/removeUser/${userId}`;
      this.http.delete(baseUrl, {observe: 'response'}).subscribe(
        (response: HttpResponse<any>) => {
          this.users.splice(index, 1);
        },
        (error) => {
          if(error.status === 404){
            console.error("Not found", error);
          } 
          else {
            console.error("Error during removing user", error);
          }
        }
      )
    }
  }

  changeAdminRole(user: myUsers, index: number){
    const userId = user.userId;
    const myId = this.getId();
    if(userId != myId){
      const baseUrl = `http://localhost:8080/changeAdminRole/${userId}`;
      this.http.put<myUsers>(baseUrl, user).subscribe(
        (response: myUsers) => {
          this.users[index] = response;
        },
        (error) => {
          if(error.status === 404){
            console.error("Not found", error);
          } 
          else {
            console.error("Error during changing role", error);
          }
        }
      )
    }
  }

  getId(){
    let Id: any;
    this.route.params.subscribe(params => {
      Id = params['Id'];
    });
    return Id;
  }

  searchByLoginOrEmail(){
    if(this.loginOrEmail){
      const param = this.loginOrEmail;
      const baseUrl = `http://localhost:8080/searchByLoginOrEmail/${param}`;
      this.http.get<myUsers[]>(baseUrl).subscribe(
        (response: myUsers[]) => {
          this.users = response;
        },
        (error) => {
          if(error.status === 404){
            console.error("Not found", error);
          } 
          else {
            console.error("Error during users downloading", error);
          }
        }
      )
    }
    else{
      this.ngOnInit();
    }
  }
}
