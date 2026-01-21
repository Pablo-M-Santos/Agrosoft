import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-card',
  imports: [CommonModule, MatIconModule],
  templateUrl: './card.html',
  styleUrl: './card.css',
})
export class Card {
  @Input() title!: string;
  @Input() value!: string | number;
  @Input() icon: string = 'info';
  @Input() iconColor: string = '#3B82F6';
  @Input() borderColor: string = '#3B82F6';
}
