import { HttpClient} from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { Lists } from '../../models/lists.model';
import { Products } from '../../models/products.model';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.css']
})
export class DetailsComponent implements OnInit {
  listName: String;
  productsList: Products[] = [];

  constructor(private router: Router, private http: HttpClient, private route: ActivatedRoute, private location: Location){}

  goBack(){
    this.location.back();
  }

  ngOnInit(){
    this.route.params.subscribe(params => {
      const listId = params['listId'];
      const baseUrl = `http://localhost:8080/getListName/${listId}`
      this.http.get<Lists>(baseUrl).subscribe(
        (response: Lists) => {
          this.listName = response.listName;
          this.getProducts(listId);
        }
      )
    });
  }

  goToSearch(){
    this.route.params.subscribe(params => {
      const listId = params['listId'];
      const userId = params['Id'];
      this.router.navigate(['my_list', userId, 'details', listId, 'search']);
    })
  }

  getProducts(listId: string){
    const baseUrl = `http://localhost:8080/getProducts/${listId}`;
    this.http.get<Products[]>(baseUrl).subscribe(
      (response: Products[]) => {
        this.productsList = response;
      },
      (error) => {
        if(error.status === 404){
          console.error("Not found", error);
        } 
        else {
          console.error("Error during products downloading", error);
        }
      }
    )
  }

  changeBought(product: Products, index: number){
    let listId = this.getId();
    const baseUrl = `http://localhost:8080/changeBought/${listId}`;
    this.http.put<Products>(baseUrl, product).subscribe(
      (response: Products) => {
         this.productsList[index] = response;
      },
      (error) => {
        if(error.status === 404){
          console.error("Not found", error);
        } 
        else {
          console.error("Error during changing bought", error);
        }
      }
    )
  }

  getId(){
    let listId: any;
    this.route.params.subscribe(params => {
      listId = params['listId'];
    });
    return listId;
  }
}
  

