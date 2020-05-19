import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {
  getCurrentYear;
  constructor() { }

  getYear (){
    var d = new Date();
    this.getCurrentYear = d.getFullYear();
  }
  ngOnInit() {
    this.getYear()
  }

}
