import { RouterModule, Routes } from "@angular/router";
import { LoginComponent } from "./login/login.component";
import { RegisterComponent } from "./register/register.component";
import { NgModule } from "@angular/core";
import { MyListComponent } from "./my-list/my-list.component";
import { DetailsComponent } from "./details/details.component";
import { SearchComponent } from "./search/search.component";
import { AdminPanelComponent } from "./admin-panel/admin-panel.component";

const routes: Routes = [
    {path: '', component: LoginComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'my_list/:Id', component: MyListComponent},
    {path: 'my_list/:Id/details/:listId', component: DetailsComponent},
    {path: 'my_list/:Id/details/:listId/search', component: SearchComponent},
    {path: 'my_list/:Id/adminpanel', component: AdminPanelComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { } 