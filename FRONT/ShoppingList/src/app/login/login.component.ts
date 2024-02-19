import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { myUsers } from '../../models/myUsers.model';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  @Output() messageEvent = new EventEmitter<string>();
  userMessage: string = '';

  constructor(private formBuilder: FormBuilder, private http: HttpClient, private router: Router){
    this.loginForm = this.formBuilder.group({
      login: ['', Validators.required],
      password: ['', Validators.required],
    })
  }

  ngOnInit() {
    this.messageEvent.subscribe((message: string) => {
      this.userMessage = message; // Aktualizacja komunikatu
    });
  }

  loginUser(){
    const baseUrl = 'http://localhost:8080/login';
    if(this.loginForm.valid){
      const formData = {
        login: this.loginForm.value.login,
        password: this.loginForm.value.password,
      };

      this.http.put<myUsers>(baseUrl, formData).subscribe(
        (response: myUsers) => {
          const userId = response.userId;
          if(userId){
            this.router.navigate(['/my_list/', userId.toString()]);
          }  
        },
        (error) =>{
          if(error.status === 404){
            this.messageEvent.emit("There is an error in login or password");
            console.error("There is an error in login or password");
          }
          else{
            this.messageEvent.emit("Error while logging in");
            console.error("Error while logging in", error);
          }
        }
      );
    }
  }
}