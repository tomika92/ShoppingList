import { HttpClient, HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { Products } from '../../models/products.model';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent {

  constructor(private http: HttpClient, private route: ActivatedRoute, private location: Location){}
  productLists: Products[] = [];
  name: String = '';

  goBack(){
    this.location.back();
  }

  ngOnInit(): void{
    let listId = this.getId();
    const baseUrl = `http://localhost:8080/getAllProducts/${listId}`;
    this.http.get<Products[]>(baseUrl).subscribe(
      (resposne : Products[]) => {
        this.productLists = resposne;
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

  changeBoughtStatus(product: Products, index: number){
    let listId = this.getId();
    if(product.bought === 0){
      this.addProductToList(product, listId, index);
    }
    else{
      this.removeProductFromList(product, listId, index);
    }
  }

  removeProductFromList(product: Products, listId: string, index: number) {
    const baseUrl = `http://localhost:8080/removeProductFromList/${listId}/${product.productId}`;
    this.http.delete<Products>(baseUrl).subscribe(
      (response: Products) => {
        this.productLists[index] = response;
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

  addProductToList(product: Products, listId: string, index: number){
    const baseUrl = `http://localhost:8080/addProductToList/${listId}`;
    this.http.post<Products>(baseUrl, product).subscribe(
      (response: Products) => {
        this.productLists[index] = response;
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

  searchByName(){
    if(this.name){
      let listId = this.getId();
      const param = this.name;
      const baseUrl = `http://localhost:8080/searchByName/${listId}?query=${param}`;
      this.http.get<Products[]>(baseUrl).subscribe(
        (response: Products[]) => {
          this.productLists = response;
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
    else{
      this.ngOnInit();
    }
  }

  getId(){
    let listId: any;
    this.route.params.subscribe(params => {
      listId = params['listId'];
    });
    return listId;
  }
}