import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ForumService } from '../../core/services/forum.service';
import { CreatePostRequest, UpdatePostRequest, CategoryDto, DEFAULT_FORUM_CATEGORIES } from '../../core/models/post.dto';
import { AuthService } from '../../core/services/auth.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-post-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './post-form.component.html',
  styleUrls: ['./post-form.component.scss']
})
export class PostFormComponent implements OnInit {
  postForm: FormGroup;
  categories: CategoryDto[] = [];
  isEdit = false;
  postId?: number;
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private forumService: ForumService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      content: ['', [Validators.required, Validators.minLength(10)]],
      categoryId: [null as number | null]
    });
  }

  ngOnInit(): void {
    this.loadCategories();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.postId = +id;
      this.loadPost();
    }
  }

  loadCategories(): void {
    this.forumService.getCategories().subscribe({
      next: (list) => {
        this.categories = list?.length ? list : DEFAULT_FORUM_CATEGORIES;
        this.cdr.markForCheck();
      },
      error: () => {
        this.categories = DEFAULT_FORUM_CATEGORIES;
        this.cdr.markForCheck();
      }
    });
  }

  loadPost(): void {
    this.loading = true;
    this.error = '';
    this.forumService.getPostById(this.postId!).pipe(
      finalize(() => {
        setTimeout(() => {
          this.loading = false;
          this.cdr.detectChanges();
        }, 0);
      })
    ).subscribe({
      next: (post) => {
        this.postForm.patchValue({
          title: post.title,
          content: post.content,
          categoryId: post.categoryId ?? null
        });
      },
      error: () => {
        this.error = 'Failed to load post.';
      }
    });
  }

  onSubmit(): void {
    if (this.postForm.invalid) return;

    this.loading = true;
    this.error = '';
    const raw = this.postForm.value;
    const request = {
      ...raw,
      categoryId: raw.categoryId || undefined
    };

    const clearLoading = () => {
      setTimeout(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }, 0);
    };

    if (this.isEdit) {
      this.forumService.updatePost(this.postId!, request as UpdatePostRequest).subscribe({
        next: () => {
          clearLoading();
          this.router.navigate([this.getForumBasePath(), this.postId]);
        },
        error: (err) => {
          this.error = (err.error && typeof err.error === 'string') ? err.error : 'Failed to update post.';
          clearLoading();
        }
      });
    } else {
      this.forumService.createPost(request as CreatePostRequest).subscribe({
        next: (post) => {
          clearLoading();
          this.router.navigate([this.getForumBasePath(), post.id]);
        },
        error: (err) => {
          this.error = (err.error && typeof err.error === 'string') ? err.error : 'Failed to create post.';
          clearLoading();
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate([this.getForumBasePath()]);
  }

  private getForumBasePath(): string {
    const role = this.authService.currentUser?.role;

    if (role === 'ADMIN') {
      return '/admin/forum';
    }
    if (role === 'PATIENT') {
      return '/patient/forum';
    }
    if (role === 'CAREGIVER') {
      return '/caregiver/forum';
    }
    if (role === 'PROVIDER') {
      return '/provider/forum';
    }

    return '/homePage';
  }
}