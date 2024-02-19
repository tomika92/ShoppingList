export class Products {
    productId: number;
    productName: string;
    bought: number

    constructor(productId: number, productName: string, bought: number) {
        this.productId = productId;
        this.productName = productName;
        this.bought = bought;
      }
  }