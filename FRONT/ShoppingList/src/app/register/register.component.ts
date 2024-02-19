import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})

export class RegisterComponent {
  registerForm: FormGroup;
  @Output() messageEvent = new EventEmitter<string>();
  userMessage: string = '';

  constructor(private formBuilder: FormBuilder, private http: HttpClient){
    this.registerForm = this.formBuilder.group({
      email: ['',[Validators.required, Validators.email]],
      login: ['', Validators.required],
      password: ['', Validators.required],
      repeat_password: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.messageEvent.subscribe((message: string) => {
      this.userMessage = message; // Aktualizacja komunikatu
    });
  }

  passwordsMatchValidator(){
    const passwordControl = this.registerForm.value.password;
    const repeatPasswordControl = this.registerForm.value.repeat_password;

    if(passwordControl === repeatPasswordControl){
      this.messageEvent.emit("");
      return null; //zgodne
    } else {
      this.messageEvent.emit("The passwords are not identical!");
      return 1 
    }
  }

  registerUser(){
    const num = this.passwordsMatchValidator();
    if(num == 1) {
      return;
    }
    
    const baseUrl = 'http://localhost:8080/register';
    if(this.registerForm.valid){
      const formData = {
        email: this.registerForm.value.email,
        login: this.registerForm.value.login,
        password: this.registerForm.value.password,
      };

      this.http.post(baseUrl, formData).subscribe(
        (response: any) => {
            this.messageEvent.emit("Registered as an user");
        },
        (error) =>{
          if(error.status === 302){
            this.messageEvent.emit("There is user with this email or login");
            console.error("There is user with this email or login");
          }
          else{
            this.messageEvent.emit("Error during registration");
            console.error("Error during registration", error);
          }
        }
      );
    }
  }
}
